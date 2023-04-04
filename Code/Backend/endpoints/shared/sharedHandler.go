package shared

import (
	paths "backend/constants"
	"backend/globals"
	"encoding/json"

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

	/* var fkTables []string
	var fkFilters []string
	var nameEndpoints []string
	var specificSelects []string */
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
		//joinData["main"] = append(joinData["main"], "transaction", "", "*") //WHAT TABLE IS THE SQL REQUEST FOR?
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
		//joinData["main"] = append(joinData["main"], "employee", "", "*") //WHAT TABLE IS THE SQL REQUEST FOR?
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

		//SQL, sqlArgs, err := globals.ConvertUrlToSql(r, activeTable, fkTables, fkFilters, nameEndpoints, specificSelects)
		SQL, sqlArgs, err := globals.ConvertUrlToSql(r, joinData, keys, activeTable)

		//SQL, sqlArgs, err := globals.ConvertUrlToSql(r, activeTable, []string{}, []string{}, []string{})
		if err != nil {
			http.Error(w, "Error in converting url to sql: "+err.Error(), http.StatusUnprocessableEntity)
			return
		}

		res, err := globals.QueryJSON(globals.DB, SQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching Data.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding Data.", http.StatusInternalServerError)
		}
	// POST method
	case http.MethodPost:
		/// SEND REQUEST TO GENERIC POST REQUEST, RECIEVE AS "res, err"

		/* // Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding transactions.", http.StatusInternalServerError)
		} */
	// PUT method
	case http.MethodPut:
	/// SEND REQUEST TO GENERIC PUT REQUEST, RECIEVE AS "res, err"

	/* // Set header and encode to writer
	w.Header().Set("Content-Type", "application/json")
	err = json.NewEncoder(w).Encode(res)
	if err != nil {
		http.Error(w, "Error encoding transactions.", http.StatusInternalServerError)
	} */

	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}

}

/**
 *	Handler for 'transactions' endpoint.
 */
/* func HandlerTransactions(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "transaction"
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_TRANSACTION_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	// Split the path on each "/", unless the path is blank
	args := []string{}
	if len(escapedPath) > 0 {
		args = strings.Split(escapedPath, "/")
	}

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodGet:
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{"Client.client_Name AS customer_name", "Location.name AS inventory_name", "employee.employee_alias AS responsible_name"}, []string{"client ON transaction.client_id = client.client_id", "location ON transaction.inventory = location.location_id", "employee ON transaction.responsible_id = employee.employee_id"}, "LEFT JOIN")
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching transactions.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding transactions.", http.StatusInternalServerError)
		}
	// POST method
	case http.MethodPost:
		// If there's not enough args, return
		if len(args) < 1 {
			http.Error(w, "Not enough arguments, read the documentation for more information.", http.StatusUnprocessableEntity)
			return
		}

	// Other method
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
} */
