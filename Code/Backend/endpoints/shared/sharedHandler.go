package shared

import (
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
	tableNames = append(tableNames, "transaction", "client", "container", "handler", "act")
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

	////////////////////////////////////
	/// CHECK FOR AUTH TOKEN PERMISSIONS
	////////////////////////////////////

	// Redirect to generic function based on url
	switch r.Method {

	// GET method
	case http.MethodGet:
		/// SEND REQUEST TO GENERIC GET REQUEST, RECIEVE AS "res, err"

		/* // Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding transactions.", http.StatusInternalServerError)
		} */
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
func HandlerTransactions(w http.ResponseWriter, r *http.Request) {
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
}

/**
 *	Handler for 'Act' endpoint.
 */
func HandlerActs(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "act"
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_ACT_PATH):]

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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{}, []string{}, "")
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching clients.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding clients.", http.StatusInternalServerError)
		}

		// PUT method
	case http.MethodPut:
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
}

/**
 *	Handler for 'Model' endpoint.
 */
func HandlerModel(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "container_model"
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_MODEL_PATH):]

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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{}, []string{}, "")
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching clients.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding clients.", http.StatusInternalServerError)
		}

		// PUT method
	case http.MethodPut:
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
}

/**
 *	Handler for 'Clients' endpoint.
 */
func HandlerClients(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "client"
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_CLIENTS_PATH):]

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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{}, []string{}, "")
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching clients.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding clients.", http.StatusInternalServerError)
		}

		// PUT method
	case http.MethodPut:
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
}

/**
 *	Handler for 'Clients' endpoint.
 */
func HandlerLocation(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "location"
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_LOCATION_PATH):]

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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{}, []string{}, "")
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching clients.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding clients.", http.StatusInternalServerError)
		}

		// PUT method
	case http.MethodPut:
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
}

/**
 *	Handler for 'Users' endpoint.
 *
 * 	NB: THIS IS TEMPORARILY JUST GET/POST/PUT FOR USERS. NOT LOGIN
 */
func HandlerUsers(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "employee"
	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.MOBILE_LOGIN_PATH):]

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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{"location.name AS location_name"}, []string{"Location ON employee.location_id = Location.location_id"}, "LEFT JOIN")
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching clients.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding clients.", http.StatusInternalServerError)
		}

		// PUT method
	case http.MethodPut:
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
}

/**
 *	Handler for 'container' endpoint.
 */
func HandlerContainer(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "container"

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.PUBLIC_CONTAINER_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	// Split the path on each "/", unless the path is blank
	/* args := []string{}
	if len(escapedPath) > 0 {
		args = strings.Split(escapedPath, "/")
	} */

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodGet:
		sqlQuery, sqlArgs, err := globals.ConvertUrlToSql(r, tableName, []string{"Client.client_Name AS customer_name", "Location.name AS location_name"}, []string{"Client ON Container.at_client = Client.client_ID", "Location ON Container.at_inventory = Location.location_id;"}, "LEFT JOIN")
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
}
