package globals

import (
	"database/sql"
	"encoding/json"
	"errors"
	"fmt"
	"math"
	"net"
	"net/http"
	"os"
	"sort"
	"strconv"
	"strings"
)

/**
 *	Flattens a map slice.
 *
 *	@param mapSlice - The map slice
 *
 *	@return The map slice, flattened.
 */
func flattenMapSlice(mapSlice []map[string]interface{}) map[string]([]interface{}) {
	// Reorder into format: ["propertyName":[value1, value2, value3]]
	// (And assembly prop query)
	props_values := make(map[string]([]interface{}))

	var props []string
	for _, kvp := range mapSlice {
		for k, v := range kvp {
			// Check if prop already exists in props
			propExists := false
			for _, prop := range props {
				if prop == k {
					propExists = true
					break
				}
			}
			if !propExists {
				props = append(props, k)
			}

			props_values[k] = append(props_values[k], v)
		}
	}

	return props_values
}

/**
 *	Parses a value, removing the in-built scientific notation of GoLang.
 *	For example: Parsing 123456789 becomes "123456789" instead of 1.23456789e8, and 12345678.9 becomes "12345678.90000" instead of 1.23456789e7
 *	Non-numbers are parsed like normal.
 *
 *	@param any - Value to parse.
 *
 *	@return The parsed value, devoid of scientific notation.
 */
func RemoveScientificNotation(any interface{}) string {
	parsed := ""
	switch any.(type) {
	case float64, float32:
		// Parse as float...
		parsed = fmt.Sprintf("%f", any)

		// ...Then convert the float to int if it doesn't change it's value
		if argVal64, ok := any.(float64); ok {
			if argVal64 == math.Floor(argVal64) {
				parsed = fmt.Sprint(int(argVal64))
			}
		} else if argVal32, ok := any.(float32); ok {
			if float64(argVal32) == math.Floor(float64(argVal32)) {
				parsed = fmt.Sprint(int(argVal64))
			}
		}

	default:
		parsed = fmt.Sprintf("%v", any)
	}

	// Return
	return parsed
}

/**
 *	Finds the origin table of a column, if given by joinData.
 *
 *	@param column - The column name.
 *	@param joinData - The joinData to search through.
 *
 *	@return The table which the column originates from.
 */
func FindOriginTable(column string, joinData map[string][]string) string {
	// Find which table the given field belongs to
	belongsToTable := ""
	for t, tf := range joinData {
		for _, s := range tf {
			if column == s {
				belongsToTable = t
				break
			}
		}
		if belongsToTable != "" {
			break
		}
	}

	// If not found, assume the field belongs to the main table
	if belongsToTable == "" {
		belongsToTable = joinData["main"][0]
	}

	// Return
	return belongsToTable
}

/**
 *	Opens a socket listening for clients.
 *
 *	@param serverHost - The server address.
 *	@param serverPort - The port to listen on.
 *	@param serverType - The protocol. TCP is standard.
 */
func ListenForClients(serverHost string, serverPort string, serverType string) {
	fmt.Println("Server Running...")

	server, err := net.Listen(serverType, serverHost+":"+serverPort)
	if err != nil {
		fmt.Println("Error listening:", err.Error())
		os.Exit(1)
	}
	defer server.Close()

	fmt.Println("Listening on " + serverHost + ":" + serverPort)

	fmt.Println("Waiting for client...")
	for {
		connection, err := server.Accept()
		if err != nil {
			fmt.Println("Error accepting: ", err.Error())
			os.Exit(1)
		}
		fmt.Println("client connected")
		go ProcessClient(connection)
	}
}

/**
 *	Processes a client.
 *
 *	@param connection - The connection to the client.
 */
func ProcessClient(connection net.Conn) {
	defer connection.Close()

	for {
		buffer := make([]byte, 1024)
		mLen, err := connection.Read(buffer)
		if err != nil {
			fmt.Println("Error reading:", err.Error())
			fmt.Println("(client shut down)")
			return
		}

		fmt.Println("Received: ", string(buffer[:mLen]))
	}
}

/**
 *	Sends a query to the database.
 *
 *	@param db - The database.
 *	@param query - The query, using '?' symbols for arguments.
 *	@param queryArgs - The query arguments, in order.
 *	@param w - The response writer to write back to.
 *
 *	@returns The result as an interface.
 */
func QueryJSON(db *sql.DB, query string, queryArgs []interface{}, w http.ResponseWriter) ([]map[string]interface{}, error) {
	// Query and fetch rows
	rows, err := db.Query(query, queryArgs...)
	if err != nil {
		return nil, err
	}

	// Fetch columns (props/attributes)
	cols, err := rows.Columns()
	if err != nil {
		return nil, err
	}
	cols_amt := len(cols)

	// Make list of interfaces
	var res []map[string]interface{}
	for rows.Next() {
		// Create an interface and get pointers to each of its fields
		values := make([]interface{}, cols_amt)
		valuesPtrs := make([]interface{}, cols_amt)
		for i := range values {
			valuesPtrs[i] = &values[i]
		}

		// Scan data to it
		rows.Scan(valuesPtrs...)

		// Parse the raw data that's just been scanned
		row_current := make(map[string]interface{})

		for i, v := range values {
			col_current := cols[i]
			switch v.(type) {
			case []uint8:
				v_bytes := v.([]byte)
				if v_float, ok := strconv.ParseFloat(string(v_bytes), 64); ok == nil {
					row_current[col_current] = v_float
				} else if v_bool, ok := strconv.ParseBool(string(v_bytes)); ok == nil {
					row_current[col_current] = v_bool
				} else if fmt.Sprintf("%T", string(v_bytes)) == "string" {
					row_current[col_current] = string(v_bytes)
				} else {
					fmt.Println("Failed to parse data: ", v_bytes)
				}
			default:
				row_current[col_current] = v
			}
		}

		// And append to the final result
		res = append(res, row_current)
	}

	// Return
	return res, nil
}

//Below creates an "exclusive query" by using AND, could be swapped to "Inclusive" by using OR

/*
ConvertUrlToSql takes an HTTP request and generates an SQL query with values separated based on the parameters provided.
The SQL query GETS entries from the provided activeTable and its related foreign key tables.

@param r: a pointer to the HTTP request
@param joinData: a map where the key is a string representing the table name and any foreign key references, and the value is a slice containing:
- the name of the table
- the name of the primary key for that table
- any data values requested from that table
@param keys: a slice of strings representing the keys in the joinData map
@param table: The main table which data is being queried from

@returns: a string representing the SQL query with placeholders
@returns: a slice of interface{} containing the values to fit the SQL query
@returns: an error if there are any issues with the request or query construction
*/

func ConvertUrlToSql(r *http.Request, joinData map[string][]string, keys []string) (string, []interface{}, error) {
	var (
		sqlArgs   []interface{}
		sqlSelect string
		sqlJoin   string
	)

	table := joinData["main"][0]

	for _, key := range keys {
		// Extract values from data
		data := joinData[key]
		tableName := data[0]
		primaryKey := data[1]
		dataIWant := data[2:]

		// Extract target table name from key
		targetTableName := strings.Split(key, ":")[0]

		// Construct SQL JOIN statement
		if key != "main" {
			sqlJoin += fmt.Sprintf("LEFT JOIN %s ON %s.%s = %s.%s ", targetTableName, tableName, primaryKey, targetTableName, primaryKey)
		}

		// Construct SQL SELECT statement
		for _, val := range dataIWant {
			if key == "main" {
				sqlSelect += fmt.Sprintf("%s.%s, ", joinData[key][0], val)
			} else {
				sqlSelect += fmt.Sprintf("%s.%s, ", key, val)
			}
		}
	}

	// Construct SQL WHERE statement
	urlData := r.URL.Query()
	urlDataKeys := make([]string, 0, len(urlData))
	for k := range urlData {
		urlDataKeys = append(urlDataKeys, k)
	}
	sort.Strings(urlDataKeys)

	var queryWhere strings.Builder

	// Declare start- and end date for later
	var (
		startDates []string
		endDates   []string
	)

	// Iterate each key (field name) and value (filter after)
	for _, k := range urlDataKeys {
		v := urlData[k]

		// Save and skip over start- and end date fields
		if k == "start_date" {
			startDates = v
			continue
		} else if k == "end_date" {
			endDates = v
			continue
		}

		// Find which table the given field belongs to
		belongsToTable := FindOriginTable(k, joinData)

		// If found, add field and table to query string
		if belongsToTable != "" {
			for _, vd := range v {
				if queryWhere.String() != "" {
					queryWhere.WriteString(" OR")
				}
				// Check if the value says to exclude values rather than include
				if len(vd) > 4 && vd[:4] == "not_" {
					vd = vd[4:]
					if vd == "null" || vd == "NULL" || vd == "" {
						queryWhere.WriteString(fmt.Sprintf(" %s.%s IS NOT NULL", belongsToTable, k))
					} else {
						queryWhere.WriteString(fmt.Sprintf(" %s.%s != ?", belongsToTable, k))
						sqlArgs = append(sqlArgs, vd)
					}
				} else {
					if vd == "null" || vd == "NULL" || vd == "" {
						queryWhere.WriteString(fmt.Sprintf(" %s.%s IS NULL", belongsToTable, k))
					} else {
						queryWhere.WriteString(fmt.Sprintf(" %s.%s = ?", belongsToTable, k))
						sqlArgs = append(sqlArgs, vd)
					}
				}
			}
		}
	}

	// Remove trailing comma from SELECT statement
	sqlSelect = sqlSelect[:len(sqlSelect)-2]

	// Combine all SQL statements into one
	SQL := fmt.Sprintf(
		"SELECT %s FROM %s %s ",
		sqlSelect, //what we want
		table,     //what is the main table
		sqlJoin,   //where do we get extra data
	)

	// Append filters to SQL query
	if queryWhere.String() != "" {
		SQL += " WHERE " + queryWhere.String()
	}

	// Append start- and end date to SQL query
	if startDates != nil && endDates != nil {
		// Ensure the "WHERE" part has been added...
		firstWhere := queryWhere.String() == ""

		// ...and add the ranges
		for i, startDate := range startDates {
			// (Stop if the current startDate doesn't have a corresponding endDate)
			if i >= len(endDates) {
				break
			}

			endDate := endDates[i]
			if firstWhere {
				SQL += " WHERE "
				firstWhere = false
			} else {
				SQL += " OR "
			}

			SQL += table + ".date BETWEEN '" + startDate + "' AND '" + endDate + "'"
		}
	}

	return SQL, sqlArgs, nil
}

/**
 *	Takes an http request and returns an SQL query with values sepperate.
 *	The SQL query POSTS entries to the given table.
 *
 *	@param r - a pointer to the http request
 *  @param table - name of the relevant table, (should be added as request header or in the url instead)
 *
 *	@returns - an SQL string with placeholders
 *	@returns - a list of values to fit the SQL query
 *  @returns - any potential errors thrown
 */
func ConvertPostURLToSQL(r *http.Request, table string) (string, []interface{}, error) {
	// Decode body
	var data []map[string]interface{}
	err := json.NewDecoder(r.Body).Decode(&data)
	if err != nil {
		println(data)
		return "", nil, err
	}
	// Get props string and create a sorted list of its keys
	props_values := flattenMapSlice(data)
	props := make([]string, 0, len(props_values))
	for k := range props_values {
		props = append(props, k)
	}
	sort.Strings(props)

	// Iterate props_values in order and append to propsQuery
	var propsQuery strings.Builder
	for i, k := range props {
		if i > 0 {
			propsQuery.WriteString(",")
		}
		propsQuery.WriteString(k)
	}

	var args []interface{}

	// Assemble values string
	var valuesQuery strings.Builder
	for i, kvp := range data {
		if i > 0 {
			valuesQuery.WriteString("), (")
		}
		for j, prop := range props {
			if j > 0 {
				valuesQuery.WriteString(",")
			}
			v := kvp[prop]
			if v == nil {
				valuesQuery.WriteString("NULL")
				continue
			}
			args = append(args, fmt.Sprintf("%v", v))
			valuesQuery.WriteString("?")
		}
	}

	// Assemble final query and query it
	query := fmt.Sprintf("INSERT INTO `%s` (%s) VALUES (%s)", table, propsQuery.String(), valuesQuery.String())
	return query, args, nil
}

/**
 *	Takes an http request and returns an SQL query with values sepperate
 *	The SQL query PUTS(updates) entries in the given table.
 *
 *	@param r - a pointer to the http request
 *	@param joinData - a map where the key is a string representing the table name and any foreign key references, and the value is a slice containing:
 *		- the name of the table
 *		- the name of the primary key for that table
 *		- any data values requested from that table
 *	@param keys - a slice of strings representing the keys in the joinData map
 *	@param kwargs - Additional arguments presented as strings:
 *		"alterForeignTables" - Alters foreign table values rather than the main table values whenever possible
 *
 *	@returns - an SQL string with placeholders
 *	@returns - a list of values to fit the SQL query
 *  @returns - any potential errors thrown
 */
func ConvertPutURLToSQL(r *http.Request, joinData map[string][]string, keys []string, kwargs ...string) (string, []interface{}, error) {
	table := joinData["main"][0] // The table which the request is aimed at

	// Additional arguments
	alterForeignTables := false
	for _, kwarg := range kwargs {
		if kwarg == "alterForeignTables" {
			alterForeignTables = true
		}
	}

	// Decode body
	var data []map[string]interface{}

	err := json.NewDecoder(r.Body).Decode(&data)
	if err != nil {
		println("error decode")
		return "", nil, err
	}

	// Get props
	props_values := flattenMapSlice(data)
	props := make([]string, 0, len(props_values))
	for k := range props_values {
		props = append(props, k)
	}
	sort.Strings(props)

	// TODO: Add exception for when NO values are given
	// OR primary_key's value isnt found in the JSON data

	var queryPref strings.Builder
	queryPref.WriteString(fmt.Sprintf("UPDATE %s ", table))

	// Add inner joins
	for _, key := range keys {
		if key == "main" {
			continue
		}

		// Extract values from data
		data := joinData[key]
		tableName := data[0]
		primaryKey := data[1]

		// Extract target table name from key
		targetTableName := strings.Split(key, ":")[0]

		// Construct SQL JOIN statement
		queryPref.WriteString(fmt.Sprintf("LEFT JOIN %s ON %s.%s = %s.%s ", targetTableName, tableName, primaryKey, targetTableName, primaryKey))
	}

	queryPref.WriteString(" SET")

	it := 0
	var args []interface{}
	for _, property := range props {
		if property == "primary" {
			continue
		}

		// Find which table the given field belongs to, if allowed to alter foreign tables
		belongsToTable := table
		if alterForeignTables {
			belongsToTable = FindOriginTable(property, joinData)
		}

		//Ensures that if there is only one type of primary key there wont be an empty update field for that value
		delayedEntry := ""
		if it == 0 {
			delayedEntry = fmt.Sprintf(" %s.`%s` = CASE", belongsToTable, property)
		} else {
			delayedEntry = fmt.Sprintf(", %s.`%s` = CASE", belongsToTable, property)
		}
		prevVal := ""
		for index, val := range props_values["primary"] {
			if propVal, ok := val.(string); ok {
				if prevVal == propVal {
					continue
				}
				prevVal = propVal
				if propVal != property {
					it++
					queryPref.WriteString(delayedEntry)
					queryPref.WriteString(fmt.Sprintf(" WHEN %s.`%s` = ? THEN ?", FindOriginTable(propVal, joinData), propVal))

					// If the args are numbers, don't use scientific connotation
					args = append(args, RemoveScientificNotation(props_values[propVal][index]))
					args = append(args, RemoveScientificNotation(props_values[property][index]))

					if index+1 != len(props_values["primary"]) {
						queryPref.WriteString(fmt.Sprintf(" ELSE `%s`", property))
					}
					queryPref.WriteString(" END")
				}
			} else {
				println("error prop value")

				return "", nil, errors.New("error asserting props_values as string")
			}
		}
	}

	for p, property := range props_values["primary"] {
		if propVal, ok := property.(string); ok {
			belongsToTable := FindOriginTable(propVal, joinData)

			if p == 0 {
				queryPref.WriteString(fmt.Sprintf(" WHERE %s.`%s` = '%s'", belongsToTable, propVal, RemoveScientificNotation(props_values[propVal][p])))
			} else {
				queryPref.WriteString(fmt.Sprintf(" OR %s.`%s` = '%s'", belongsToTable, propVal, RemoveScientificNotation(props_values[propVal][p])))
			}
		} else {

			println("error prop value as string")
			return "", nil, errors.New("error asserting props_values as string")
		}
	}

	// Return
	queryPref.WriteString(";")
	return queryPref.String(), args, nil
}

/**
 *	Takes an http request and returns an SQL query with values sepperate
 *	The SQL query DELETES entries in the given table.
 *
 *	@param r - a pointer to the http request
 *	@param joinData - a map where the key is a string representing the table name and any foreign key references, and the value is a slice containing:
 *		- the name of the table
 *		- the name of the primary key for that table
 *		- any data values requested from that table
 *	@param keys - a slice of strings representing the keys in the joinData map
 *
 *	@returns - an SQL string with placeholders
 *	@returns - a list of values to fit the SQL query
 *  @returns - any potential errors thrown
 */
func ConvertDeleteURLToSQL(r *http.Request, joinData map[string][]string, keys []string) (string, []interface{}, error) {
	table := joinData["main"][0] // The table which the request is aimed at

	// DELETE statement
	query := fmt.Sprintf("DELETE P FROM %s P", table)
	var args []interface{}

	// JOIN statement
	for _, key := range keys {
		if key == "main" {
			continue
		}

		// Extract values from data
		data := joinData[key]
		tableName := data[0]
		primaryKey := data[1]

		// Use P rather than the main table name
		if tableName == table {
			tableName = "P"
		}

		// Extract target table name from key
		targetTableName := strings.Split(key, ":")[0]

		// Add left join
		query += fmt.Sprintf(
			"\nLEFT JOIN %s ON %s.%s = %s.%s",
			targetTableName,
			tableName,
			primaryKey,
			targetTableName,
			primaryKey,
		)
	}

	// Set up start- and end date variables for later
	var (
		startDates []string
		endDates   []string
	)

	// Iterate query keys- and values
	urlQuery := r.URL.Query()
	urlQueryKeys := make([]string, 0, len(urlQuery))
	for k := range urlQuery {
		urlQueryKeys = append(urlQueryKeys, k)
	}
	sort.Strings(urlQueryKeys)

	var queryWhere strings.Builder
	for _, k := range urlQueryKeys {
		v := urlQuery[k]
		// Save and skip over start- and end date fields
		if k == "start_date" {
			startDates = v
			continue
		} else if k == "end_date" {
			endDates = v
			continue
		}

		// Find which table the given field belongs to
		belongsToTable := FindOriginTable(k, joinData)
		if belongsToTable == table {
			belongsToTable = "P"
		}

		// If found, add field and table to query string
		if belongsToTable != "" {
			for _, vd := range v {
				if queryWhere.String() != "" {
					queryWhere.WriteString(" OR")
				}
				queryWhere.WriteString(fmt.Sprintf("\n\t%s.%s = ?", belongsToTable, k))
				args = append(args, vd)
			}
		}
	}

	// Add WHERE statement
	if queryWhere.String() != "" {
		query += "\nWHERE" + queryWhere.String()
	}

	// Append start- and end date to SQL query
	if startDates != nil && endDates != nil {
		// Ensure the "WHERE" part has been added...
		firstWhere := queryWhere.String() == ""

		// ...and add the ranges
		for i, startDate := range startDates {
			// (Stop if the current startDate doesn't have a corresponding endDate)
			if i >= len(endDates) {
				break
			}

			endDate := endDates[i]
			if firstWhere {
				query += "\nWHERE"
				firstWhere = false
			} else {
				query += " OR"
			}

			query += "\n\tP.date BETWEEN '" + startDate + "' AND '" + endDate + "'"
		}
	}

	// Return
	return query, args, nil
}
