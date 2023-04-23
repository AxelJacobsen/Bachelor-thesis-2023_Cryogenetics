package shared

import (
	"backend/constants"
	paths "backend/constants"
	"backend/globals"
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
		/// SEND REQUEST TO GENERIC GET REQUEST, RECIEVE AS "res, err"
		SQL, sqlArgs, err := globals.ConvertUrlToSql(r, joinData, keys)
		if err != nil {
			http.Error(w, "Error in converting url to sql: "+err.Error(), http.StatusUnprocessableEntity)
			return
		}

		res, err := globals.QueryJSON(globals.DB, SQL, sqlArgs, w)
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

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
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

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
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

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
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
			sliceValue, err := globals.QueryJSON(globals.DB, query, queryArgs, w)
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
