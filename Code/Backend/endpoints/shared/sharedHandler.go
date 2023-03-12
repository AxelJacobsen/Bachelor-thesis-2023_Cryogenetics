package shared

import (
	paths "backend/constants"
	"backend/globals"
	"encoding/json"
	"net/http"
	"strings"
)

/**
 *	Handler for 'transactions' endpoint.
 */
func HandlerTransactions(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

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
		// If there's not enough args, return
		if len(args) < 1 {
			http.Error(w, "Not enough arguments, read the documentation for more information.", http.StatusUnprocessableEntity)
			return
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
 *	Handler for 'inventory' endpoint.
 */
func HandlerInventory(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

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
		// If there's not enough args, return
		if len(args) < 1 {
			http.Error(w, "Not enough arguments, read the documentation for more information.", http.StatusUnprocessableEntity)
			return
		}
		// POST method
	case http.MethodPost:
		// If there's not enough args, return
		if len(args) < 1 {
			http.Error(w, "Not enough arguments, read the documentation for more information.", http.StatusUnprocessableEntity)
			return
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
		// If there's not enough args, return
		if len(args) < 0 { // TEMP
			http.Error(w, "Not enough arguments, read the documentation for more information.", http.StatusUnprocessableEntity)
			return
		}

		// --------- TEMP EXAMPLE ON HOW TO FETCH (ADMINS) FROM DB --------- //
		res, err := globals.QueryJSON(globals.DB, "SELECT * FROM `admin`", w)
		if err != nil {
			http.Error(w, "Error fetching admins.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding admins.", http.StatusInternalServerError)
		}
		// ----------------------------------------------------------------- //

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
 */
func HandlerUsers(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

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
		// If there's not enough args, return
		if len(args) < 1 {
			http.Error(w, "Not enough arguments, read the documentation for more information.", http.StatusUnprocessableEntity)
			return
		}

		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName)
		if err != nil {
			http.Error(w, "Error in converting url to sql", http.StatusUnprocessableEntity)
		}

		res, err := globals.QueryJSON(globals.DB, containerSQL, sqlArgs, w)
		if err != nil {
			http.Error(w, "Error fetching admins.", http.StatusInternalServerError)
			return
		}

		// Set header and encode to writer
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding admins.", http.StatusInternalServerError)
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
