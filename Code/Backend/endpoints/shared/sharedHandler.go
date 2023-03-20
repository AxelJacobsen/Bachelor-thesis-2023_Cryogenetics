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
 *	Handler for 'transactions' endpoint.
 */
func HandlerTransactions(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	tableName := "transaction"
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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName)
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
		containerSQL, sqlArgs, err := globals.ConvertUrlToSql(r, tableName)
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
		// Decode body
		var data []map[string]interface{}

		err := json.NewDecoder(r.Body).Decode(&data)
		if err != nil {
			http.Error(w, "Could not decode body.", http.StatusUnprocessableEntity)
			return
		}
		var propsQuery strings.Builder
		i := 0

		// Reorder into format: ["propertyName":[value1, value2, value3]]
		// (And assembly prop query)
		props_values := make(map[string]([]interface{}))

		var props []string
		for _, kvp := range data {
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
					if i > 0 {
						propsQuery.WriteString(", ")
					}
					propsQuery.WriteString(fmt.Sprintf("`%s`", k))
					i++
				}

				props_values[k] = append(props_values[k], v)
			}
		}

		// Assemble values string
		var valuesQuery strings.Builder
		for i, kvp := range data {
			if i > 0 {
				valuesQuery.WriteString("), (")
			}
			for j, prop := range props {
				if j > 0 {
					valuesQuery.WriteString(", ")
				}
				v := kvp[prop]
				if v == nil {
					valuesQuery.WriteString("NULL")
					continue
				}
				valuesQuery.WriteString(fmt.Sprintf("'%v'", v))
			}
		}

		var queryPref strings.Builder
		queryPref.WriteString(fmt.Sprintf("UPDATE %s SET", tableName))

		it := 0
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
						queryPref.WriteString(delayedEntry)
						queryPref.WriteString(fmt.Sprintf(" WHEN `%s` = '%v' THEN '%v'", propVal, props_values[propVal][index], props_values[property][index]))
						if index+1 != len(props_values["primary"]) {
							queryPref.WriteString(fmt.Sprintf(" ELSE `%s` END", property))
						}
					}
				} else {
					http.Error(w, "Error asserting props_values as string.", http.StatusInternalServerError)
					return
				}
			}

			it++
		}

		for p, property := range props_values["primary"] {
			if propVal, ok := property.(string); ok {
				if p == 0 {
					queryPref.WriteString(fmt.Sprintf(" WHERE `%s` = '%v'", propVal, props_values[propVal][p]))
				} else {
					queryPref.WriteString(fmt.Sprintf(" OR `%s` = '%v'", propVal, props_values[propVal][p]))
				}
			} else {
				http.Error(w, "Error asserting props_values as string.", http.StatusInternalServerError)
				return

			}
		}

		queryPref.WriteString(";")

		/* 		// Assemble final query and query it
		   		query := fmt.Sprintf("UPDATE %s (%s) VALUES (%s)", tableName, propsQuery.String(), valuesQuery.String())
		   		fmt.Println("query: ", query) */
		var args []interface{}
		res, err := globals.QueryJSON(globals.DB, queryPref.String(), args, w)
		if err != nil {

			fmt.Println(err)
			http.Error(w, "Error fetching containers.", http.StatusInternalServerError)
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
