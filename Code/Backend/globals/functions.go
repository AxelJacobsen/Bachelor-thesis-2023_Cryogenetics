package globals

import (
	"database/sql"
	"encoding/json"
	"errors"
	"fmt"
	"net"
	"net/http"
	"os"
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

/**
 * Takes an http request and returns an SQL query with values separate.
 * The SQL query GETS entries from the given table.
 *
 * @param r - a pointer to the http request
 * @param table - name of the relevant table, (should be added as request header or in the url instead)
 * @param FKeys - SQL query of the foreign keys in the table for joining (e.g.["Client.client_Name AS client_name,"])
 * @param joins - a slice of strings representing the tables to join on (e.g. ["Client ON Container.at_client = Client.client_ID"])
 * @param joinType - a string representing the type of join (e.g. "LEFT JOIN")
 *
 * @returns - an SQL string with placeholders
 * @returns - a list of values to fit the SQL query
 * @returns - any potential errors thrown
 */
func ConvertUrlToSql(r *http.Request, table string, FKeys []string, joins []string, joinType string) (string, []interface{}, error) {
	// Get url values
	urlData := r.URL.Query()
	var emptyRet []interface{}
	//Empty table name
	if len(table) <= 0 {

		return "", emptyRet, errors.New("couldn't write to string in SQL constructor")
	}

	//Initiate builder
	var query strings.Builder

	if len(FKeys) > 0 {
		var tempQuery = "SELECT " + table + ".*"
		for _, FKey := range FKeys {
			tempQuery += ", " + FKey
		}
		tempQuery += " FROM " + table + " "

		query.WriteString(tempQuery)

	} else {
		//Start with basic format
		query.WriteString(fmt.Sprintf("SELECT %s FROM `%s` ", table+".*", table))
	}

	//Add joins if specified
	if len(joins) > 0 {
		for _, join := range joins {
			query.WriteString(fmt.Sprintf("%s %s ", joinType, join))
		}
	}

	//If there are no parameters
	if len(urlData) <= 0 {
		//Not necessarily an error, but should still break
		return query.String(), emptyRet, nil
	}

	//Add WHERE clause
	query.WriteString("WHERE ")

	//Prep args container
	var argList []interface{}

	//Placeholder counter
	i := 1

	for key, value := range urlData {
		if len(value) == 1 {
			//Single value under key
			argList = append(argList, value[0])
			_, err := query.WriteString(fmt.Sprintf("%s = ?%d OR ", key, i))

			if err != nil {
				return "", emptyRet, errors.New("couldn't write to string in SQL constructor")
			}

			i++
		} else {
			//Multiple variables under same key
			for o := 0; o < len(value); o++ {
				//Stow the actual value to be returned separately
				argList = append(argList, value[o])
				//Overwrite inValue with a placeholder to be written into the SQL query
				value[o] = fmt.Sprintf("?%d", i)
				i++
			}
			//Write formatted placeholder to the query
			_, err := query.WriteString(fmt.Sprintf("%s IN (%s) OR ", key, strings.Join(value, ", ")))
			if err != nil {
				return "", emptyRet, errors.New("couldn't write to string in SQL constructor")
			}

		}
	}

	outQuery := query.String()[:len(query.String())-5]
	return outQuery, argList, nil
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
		return "", nil, err
	}

	// Get props string
	props_values := flattenMapSlice(data)
	var propsQuery strings.Builder
	props := make([]string, len(props_values))
	i := 0
	for k := range props_values {
		if i > 0 {
			propsQuery.WriteString(",")
		}
		propsQuery.WriteString(k)
		props[i] = k
		i++
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
 *  @param table - name of the relevant table, (should be added as request header or in the url instead)
 *
 *	@returns - an SQL string with placeholders
 *	@returns - a list of values to fit the SQL query
 *  @returns - any potential errors thrown
 */
func ConvertPutURLToSQL(r *http.Request, table string) (string, []interface{}, error) {
	// Decode body
	var data []map[string]interface{}

	err := json.NewDecoder(r.Body).Decode(&data)
	if err != nil {
		return "", nil, err
	}

	// Get props
	props_values := flattenMapSlice(data)
	props := make([]string, len(props_values))
	i := 0
	for k := range props_values {
		props[i] = k
		i++
	}

	// TODO: Add exception for when NO values are given

	var queryPref strings.Builder
	queryPref.WriteString(fmt.Sprintf("UPDATE %s SET", table))

	it := 0
	var args []interface{}
	for _, property := range props {
		if property == "primary" {
			continue
		}
		//Ensures that if there is only one type of primary key there wont be an empty update field for that value
		delayedEntry := ""
		if it == 0 {
			delayedEntry = fmt.Sprintf(" `%s` = CASE", property)
		} else {
			delayedEntry = fmt.Sprintf(", `%s` = CASE", property)
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
					queryPref.WriteString(fmt.Sprintf(" WHEN `%s` = ? THEN ?", propVal))
					args = append(args, fmt.Sprintf("%v", props_values[propVal][index]))
					args = append(args, fmt.Sprintf("%v", props_values[property][index]))
					if index+1 != len(props_values["primary"]) {
						queryPref.WriteString(fmt.Sprintf(" ELSE `%s`", property))
					}
					queryPref.WriteString(" END")
				}
			} else {
				return "", nil, errors.New("error asserting props_values as string")
			}
		}
	}

	for p, property := range props_values["primary"] {
		if propVal, ok := property.(string); ok {
			if p == 0 {
				queryPref.WriteString(fmt.Sprintf(" WHERE `%s` = '%v'", propVal, props_values[propVal][p]))
			} else {
				queryPref.WriteString(fmt.Sprintf(" OR `%s` = '%v'", propVal, props_values[propVal][p]))
			}
		} else {
			return "", nil, errors.New("error asserting props_values as string")
		}
	}

	// Return
	queryPref.WriteString(";")
	return queryPref.String(), args, nil
}
