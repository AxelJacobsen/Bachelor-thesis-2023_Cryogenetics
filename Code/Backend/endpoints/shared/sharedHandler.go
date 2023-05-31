package shared

import (
	"backend/constants"
	paths "backend/constants"
	"backend/cryptography"
	"backend/globals"
	"backend/request"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
)

/**
 *	Recieves all requests and performs security as well as
 */
func EndpointHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", constants.WEBAPP_URL)

	var tableNames []string
	tableNames = append(tableNames, "transaction", "client", "container", "handler", "act", "container_model", "container_status", "employee", "location")
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.BASE_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	// Split the path on each "/", unless the path is blank
	args := []string{}
	var activeTable string
	if len(escapedPath) > 0 {
		args = strings.Split(escapedPath, "/")

		//Check if url endpoint is legal table
		for _, i := range tableNames {
			if i == strings.ToLower(args[0]) {
				activeTable = i
			}
		}
		if activeTable == "" {
			http.Error(w, "Missing or illegal endpoint name, check spelling", http.StatusBadRequest)
			return
		}

	} else {
		http.Error(w, "Missing or illegal endpoint name", http.StatusBadRequest)
		return
	}

	joinData := make(map[string][]string)
	var keys []string

	joinData["main"] = append(joinData["main"], activeTable, "", "*") //WHAT TABLE IS THE SQL REQUEST FOR?
	keys = []string{"main"}

	////////////////////////////////////
	/// CHECK FOR AUTH TOKEN PERMISSIONS
	////////////////////////////////////

	// Redirect to generic function based on url
	switch r.Method {

	// GET method
	case http.MethodGet:
		joinData, keys = constants.SetJoinData(joinData, keys, activeTable)

		// For fetching columns
		if len(args) > 1 && args[1] == "columns" {
			sqlQuery := `SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_KEY
			FROM INFORMATION_SCHEMA.COLUMNS
			WHERE TABLE_NAME = ?`
			sqlArgs := []interface{}{activeTable}

			// Query
			res, err := request.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
			if err != nil {
				http.Error(w, "Error fetching columns for the given table "+err.Error(), http.StatusUnprocessableEntity)
				return
			}

			// Add joins (if applicable)
			if len(args) > 2 && args[2] == "all" {
				for _, k := range keys {
					if k == "main" {
						continue
					}

					joinedTableName := strings.Split(k, ":")[0]
					res = append(res, map[string]interface{}{"COLUMN_NAME": joinedTableName})
				}
			}

			// Writeback
			w.Header().Set("Content-Type", "application/json")
			err = json.NewEncoder(w).Encode(res)
			if err != nil {
				http.Error(w, "Error encoding data.", http.StatusInternalServerError)
			}

			return
		}

		/// SEND REQUEST TO GENERIC GET REQUEST, RECIEVE AS "res, err"
		SQL, sqlArgs, err := globals.ConvertUrlToSql(r, joinData, keys)
		if err != nil {
			http.Error(w, "Error in converting url to sql: "+err.Error(), http.StatusUnprocessableEntity)
			return
		}

		res, err := request.QueryJSON(globals.DB, SQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching data."+err.Error(), http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding data.", http.StatusInternalServerError)
		}
	// POST method
	case http.MethodPost:
		/// SEND REQUEST TO GENERIC POST REQUEST, RECIEVE AS "res, err"
		sqlQuery, sqlArgs, err := globals.ConvertPostURLToSQL(r, activeTable)
		if err != nil {
			http.Error(w, "Error in converting url to sql.", http.StatusUnprocessableEntity)
		}

		res, err := request.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error posting data."+err.Error(), http.StatusConflict)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding sql result.", http.StatusInternalServerError)
			return
		}
	// PUT method
	case http.MethodPut:
		joinData, keys = constants.SetJoinData(joinData, keys, activeTable)

		/// SEND REQUEST TO GENERIC PUT REQUEST, RECIEVE AS "res, err"
		sqlQuery, sqlArgs, err := globals.ConvertPutURLToSQL(r, joinData, keys)
		if err != nil {
			http.Error(w, "Error converting url to sql.", http.StatusUnprocessableEntity)
			return
		}

		res, err := request.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error putting data.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding data.", http.StatusInternalServerError)
			return
		}

	// DELETE method
	case http.MethodDelete:
		joinData, keys = constants.SetJoinData(joinData, keys, activeTable)

		// Get query
		sqlQuery, sqlArgs, err := globals.ConvertDeleteURLToSQL(r, joinData, keys)
		if err != nil {
			http.Error(w, "Error converting url to sql.", http.StatusUnprocessableEntity)
			return
		}

		res, err := request.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error deleting data.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding data.", http.StatusInternalServerError)
			return
		}

	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}

}

/**
 *	Handles GET requests for website to create Containers and Transactions
 *  THESE REQUESTS DO NOT RECIEVE ANY BODY, THEY ONLY PROVIDE THE DATA FOR "DROP DOWN MENUS"
 */
func CreateDataHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", constants.WEBAPP_URL)

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.SHARED_CREATE_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}
	var tables = []string{}
	// Split the path on each "/", unless the path is blank
	args := []string{}
	if len(escapedPath) > 0 {
		args = strings.Split(escapedPath, "/")

		// Check if url endpoint is legal table
		if strings.ToLower(args[0]) != "transaction" && strings.ToLower(args[0]) != "container" && strings.ToLower(args[0]) != "employee" {
			http.Error(w, "Missing or illegal endpoint name, check spelling", http.StatusBadRequest)
			return
		}

		switch strings.ToLower(args[0]) {
		case "transaction":
			tables = append(tables, "act", "client", "employee", "container", "location", "container_model")
		case "container":
			tables = append(tables, "container_status", "client", "location", "container_model")
		case "employee":
			tables = append(tables, "location", "employee")
		}

	} else {
		http.Error(w, "Missing or illegal endpoint name", http.StatusBadRequest)
		return
	}

	// Redirect to generic function based on url
	switch r.Method {

	// GET method
	case http.MethodGet:
		var data = make(map[string]interface{})
		for _, tableName := range tables {
			// define your SQL query
			query := fmt.Sprintf("SELECT * FROM %s", tableName)

			// define your query arguments as an empty slice of interface{}
			queryArgs := []interface{}{}
			sliceValue, err := request.QueryJSON(globals.DB, query, queryArgs, w)
			if err != nil {
				http.Error(w, "Error fetching data.", http.StatusInternalServerError)
			}
			data[tableName] = sliceValue
		}

		responseJSON, err := json.Marshal(data)
		if err != nil {
			http.Error(w, "Error Marshalling data.", http.StatusInternalServerError)
		}

		w.Header().Set("Content-Type", "application/json")
		w.Write(responseJSON)

	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
}

/**
 *	Handler for cryptography-related inquiries.
 */
func CryptographyHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", constants.WEBAPP_URL)

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.SHARED_CREATE_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	//args := strings.Split(escapedPath, "/")

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodGet:
		// Fetch private key
		privateKey, err := cryptography.FetchPrivateKey()
		if err != nil {
			http.Error(w, "Error fetching key.", http.StatusInternalServerError)
			return
		}

		// Marshal data to JSON
		responseJSON, err := json.Marshal([]map[string]string{
			{"N": privateKey.PublicKey.N.String(), "E": fmt.Sprintf("%d", privateKey.PublicKey.E)},
		})

		if err != nil {
			http.Error(w, "Error Marshalling data.", http.StatusInternalServerError)
		}

		// Write response
		w.Header().Set("Content-Type", "application/json")
		w.Write(responseJSON)
	}
}
