package globals

import (
	"database/sql"
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
func QueryJSON(db *sql.DB, query string, w http.ResponseWriter) ([]map[string]interface{}, error) {
	// Query and fetch rows
	rows, err := db.Query(query)
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

//The two functions below can be merged
//Thought maybe it was nicer this way, but i dont really see a reason to split them
//Unless we want to alter the values for whatever reasen :shrug:

/**
 *	Splits a url into a map of values
 *
 *	@param r - a pointer to the http request
 *
 *	@returns - a map[string]interface of the url values
 */
func HandleUrlParams(r *http.Request) map[string]interface{} {
	// Get url values
	urlData := r.URL.Query()

	params := make(map[string]interface{})

	for key, vals := range urlData {
		if len(vals) == 1 {
			params[key] = vals[0]
		} else {
			var valSlice []interface{}
			for _, val := range vals {
				valSlice = append(valSlice, val)
			}
			params[key] = valSlice
		}
	}
	return params
}

/**
 *	Constructs an SQL filter query based on parameters supplied in the url
 *
 *	@param table - name of the relevant table (could be added if we add a header / specific url param)
 *	@param params - contains key value pairs of url values
 *
 *	@returns - a prepared SQL string with placeholders to be filled
 *	@returns - a list of the data to fill the SQL placeholders
 *	@returns - any errors caught in the function
 */
func ConstructQuery(table string, params map[string]interface{}) (string, []interface{}, error) {
	//Initiate builder
	var query strings.Builder
	//Start with basic format
	query.WriteString("SELECT * FROM " + table + " WHERE ")

	//Prep args container
	var argList []interface{}
	//Placeholder counter
	i := 1
	//Iterate the map for key and values
	for key, value := range params {
		//if there are more than one values in the key enter if
		if valSlice, ok := value.([]interface{}); ok {
			//Prepare container
			placeHolders := make([]string, len(valSlice))
			for o := 0; o < len(valSlice); o++ {
				argList = append(argList, valSlice[o])
				//Add placeholder number into slice
				placeHolders[o] = fmt.Sprintf("$%d", i)
				i++
			}
			//Write formatted placeholder to the query
			query.WriteString(fmt.Sprintf("%s IN (%s) AND ", key, strings.Join(placeHolders, ", ")))
		} else {
			argList = append(argList, value)
			query.WriteString(fmt.Sprintf("%s = $%d AND ", key, i))
			i++
		}
	}

	//cut the last " AND " from the query
	outQuery := query.String()[:len(query.String())-5]

	//Unsure if this is necessary, but in my head it helps prevent injections when we replace placeholders later to query
	return outQuery, argList, nil
}
