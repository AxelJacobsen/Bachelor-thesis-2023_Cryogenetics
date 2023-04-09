package shared

import (
	paths "backend/constants"
	"backend/globals"
	"encoding/json"
	"fmt"

	//"fmt"
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

	}

	joinData := make(map[string][]string)
	var keys []string

	joinData["main"] = append(joinData["main"], activeTable, "", "*") //WHAT TABLE IS THE SQL REQUEST FOR?
	keys = []string{"main"}

	switch activeTable {
	case "container":
		/*
		* 	targetTableName: Name of table we want "dataIWant" and such from
		*	tablename: Name of the table that shares the primary key value with target table
		*	PrimaryKey: Name of value that is the SQL Primary key on TargetTableName
		*	dataIWant: desired data we want SQL query to include
		 */
		// 								"targetTableName" :	["tablename", "PrimaryKey",	"dataIWant", "moreDataIWant", etc...]
		joinData["client"] = append(joinData["client"], "container", "client_id", "client_name")
		joinData["location"] = append(joinData["location"], "container", "location_id", "location_name")
		joinData["container_model"] = append(joinData["container_model"], "container", "container_model_name", "liter_capacity", "refill_interval")

		//List of keys used in joinData. NEEDS TO BE IN THE SAME ORDER!
		keys = []string{"main", "client", "location", "container_model"}
	case "transaction":
		/*
		* 	targetTableName: Name of table we want "dataIWant" and such from
		*	tablename: Name of the table that shares the primary key value with target table
		*	PrimaryKey: Name of value that is the SQL Primary key on TargetTableName
		*	dataIWant: desired data we want SQL query to include
		 */
		// 								"targetTableName" :	["tablename", "PrimaryKey",	"dataIWant", "moreDataIWant", etc...]
		joinData["client"] = append(joinData["client"], "transaction", "client_id", "client_name")
		joinData["employee"] = append(joinData["employee"], "transaction", "employee_id", "employee_alias")
		joinData["location"] = append(joinData["location"], "transaction", "location_id", "location_name")
		joinData["container"] = append(joinData["container"], "container", "container_sr_number", "temp_id")
		joinData["container_model"] = append(joinData["container_model"], "container", "container_model_name", "liter_capacity")

		//List of keys used in joinData. NEEDS TO BE IN THE SAME ORDER!
		keys = []string{"main", "client", "employee", "location", "container", "container_model"}
	case "employee":
		/*
		* 	targetTableName: Name of table we want "dataIWant" and such from
		*	tablename: Name of the table that shares the primary key value with target table
		*	PrimaryKey: Name of value that is the SQL Primary key on TargetTableName
		*	dataIWant: desired data we want SQL query to include
		 */
		// 								"targetTableName" :	["tablename", "PrimaryKey",	"dataIWant", "moreDataIWant", etc...]
		joinData["location"] = append(joinData["location"], "container", "location_id", "location_name")

		//List of keys used in joinData. NEEDS TO BE IN THE SAME ORDER!
		keys = []string{"main", "location"}
	}

	////////////////////////////////////
	/// CHECK FOR AUTH TOKEN PERMISSIONS
	////////////////////////////////////

	// Redirect to generic function based on url
	switch r.Method {

	// GET method
	case http.MethodGet:
		/// SEND REQUEST TO GENERIC GET REQUEST, RECIEVE AS "res, err"
		SQL, sqlArgs, err := globals.ConvertUrlToSql(r, joinData, keys)
		if err != nil {
			http.Error(w, "Error in converting url to sql: "+err.Error(), http.StatusUnprocessableEntity)
			return
		}

		res, err := globals.QueryJSON(globals.DB, SQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching data.", http.StatusInternalServerError)
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
			http.Error(w, "Error posting data.", http.StatusInternalServerError)
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
		/// SEND REQUEST TO GENERIC PUT REQUEST, RECIEVE AS "res, err"
		sqlQuery, sqlArgs, err := globals.ConvertPutURLToSQL(r, activeTable)
		if err != nil {
			http.Error(w, "Error converting url to sql.", http.StatusUnprocessableEntity)
			return
		}
		println("SQL: " + sqlQuery)

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			fmt.Println("err: ", err)
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

	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}

}

/**
 *	Handler for 'transactions' endpoint.
 */
/* func HandlerContainer(w http.ResponseWriter, r *http.Request) {
w.Header().Add("content-type", "application/json")
tableName := "container"

// Get escaped path without base URL and remove the first character if it's a "/"
escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_CONTAINER_PATH):]

if len(escapedPath) > 0 && escapedPath[0] == '/' {
	escapedPath = escapedPath[1:]
}

// Split the path on each "/", unless the path is blank
 args := []string{}
if len(escapedPath) > 0 {
	args = strings.Split(escapedPath, "/")
} */

// Switch based on method
/*switch r.Method {

	// GET method
	case http.MethodGet:
		sqlQuery, sqlArgs, err := globals.ConvertUrlToSql(r, tableName)
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching containers.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding containers.", http.StatusInternalServerError)
		}

	// POST method
	case http.MethodPost:
		sqlQuery, sqlArgs, err := globals.ConvertPostURLToSQL(r, tableName)
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error posting containers.", http.StatusInternalServerError)
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
		sqlQuery, sqlArgs, err := globals.ConvertPutURLToSQL(r, tableName)
		if err != nil {
			http.Error(w, "Error converting url to sql.", http.StatusUnprocessableEntity)
			return
		}

		res, err := globals.QueryJSON(globals.DB, sqlQuery, sqlArgs, w)
		if err != nil {
			fmt.Println("err: ", err)
			http.Error(w, "Error putting containers.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding containers.", http.StatusInternalServerError)
			return
		}

	// Other method
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
} */
