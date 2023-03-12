package globals

import (
	"database/sql"
	"errors"
	"fmt"
	"net"
	"net/http"
	"os"
	"strconv"
	"strings"
)

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
 *	@param query - The query.
 *
 *	@returns The result as an interface.
 */
func QueryJSON(db *sql.DB, query string, queryArgs []interface{}, w http.ResponseWriter) ([]map[string]interface{}, error) {
	// Query and fetch rows

	fmt.Println(query, queryArgs)

	rows, err := db.Query(query, queryArgs)
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
			if v == nil {
				row_current[col_current] = nil
				continue
			}

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
		}

		// And append to the final result
		res = append(res, row_current)
	}

	// Return
	return res, nil
}

//Below creates an "exclusive query" by using AND, could be swapped to "Inclusive" by using OR

/**
 *	Takes an http request and returns an SQL query with values sepperate
 *
 *	@param r - a pointer to the http request
 *  @param table - name of the relevant table, (should be added as request header or in the url instead)
 *
 *	@returns - an SQL string with placeholders
 *	@returns - a list of values to fit the SQL query
 *  @returns - any potential errors thrown
 */
func ConvertUrlToSql(r *http.Request, table string) (string, []string, error) {
	// Get url values
	urlData := r.URL.Query()

	//Empty table name
	if len(table) <= 0 {
		return "", []string{}, errors.New("couldn't write to string in SQL constructor")
	}

	//If there are no parameters
	if len(urlData) <= 0 {
		//Not necesserily an error, but should still break
		noFilt := "SELECT * FROM " + table
		return noFilt, []string{}, nil
	}

	//Initiate builder
	var query strings.Builder

	//Start with basic format
	query.WriteString("SELECT * FROM " + table + " WHERE ")

	//Prep args container
	var argList []string

	//Placeholder counter
	i := 1

	for key, value := range urlData {
		if len(value) == 1 {
			//Single value under key
			argList = append(argList, value[0])
			_, err := query.WriteString(fmt.Sprintf("%s = ?%d OR ", key, i))

			if err != nil {
				return "", []string{}, errors.New("couldn't write to string in SQL constructor")
			}

			i++
		} else {
			//Multiple variables under same key
			for o := 0; o < len(value); o++ {
				//Stow the actual value to be returned seperately
				argList = append(argList, value[o])
				//Overwrite inValue with a placeholder to be written into the SQL query
				value[o] = fmt.Sprintf("?%d", i)
				i++
			}
			//Write formatted placeholder to the query
			_, err := query.WriteString(fmt.Sprintf("%s IN (%s) OR ", key, strings.Join(value, ", ")))
			if err != nil {
				return "", []string{}, errors.New("couldn't write to string in SQL constructor")
			}

		}
	}

	outQuery := query.String()[:len(query.String())-5]
	return outQuery, argList, nil
}
