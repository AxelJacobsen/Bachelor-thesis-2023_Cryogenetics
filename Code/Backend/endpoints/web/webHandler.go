package web

import (
	paths "backend/constants"
	"backend/globals"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"net/http"
)

type AdminRequest struct {
	Email        string `json:"email"`
	PasswordHash string `json:"password_hash"`
}
type Response struct {
	Success bool `json:"success"`
}

/**
 *	Handler for 'Web Login' endpoint.
 */
func HandlerWebLogin(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.WEB_LOGIN_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	var a AdminRequest

	// Try to decode the request body into the struct. If there is an error,
	// respond to the client with the error message and a 400 status code.
	err := json.NewDecoder(r.Body).Decode(&a)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	if a.Email == "" || a.PasswordHash == "" {
		http.Error(w, "Empty inputs.", http.StatusBadRequest)
		return
	}

	// Switch based on method
	switch r.Method {

	// POST method
	case http.MethodPost:
		row := globals.DB.QueryRow("SELECT * FROM `admin` WHERE `email` = ?  ", a.Email)
		var id int
		var email string
		var passwordHash string
		err = row.Scan(&id, &email, &passwordHash)
		if err != nil {
			http.Error(w, "Incorrect email", http.StatusUnauthorized)
			return
		}
		//Encrypt the password that matches the email
		hash := sha256.Sum256([]byte(passwordHash))

		//does the encryption of the password match the encrypted password the user gave?
		if hex.EncodeToString(hash[:]) != a.PasswordHash {
			http.Error(w, "Incorrect email or password.", http.StatusUnauthorized)
			return
		}

		// Send back true if the email and password match
		json.NewEncoder(w).Encode(map[string]bool{"authenticated": true})
		return

	// Other method
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
}

/**
 *	Handler for 'verification'(admin) endpoint.
 */
func HandlerVerification(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.ADMIN_VERIFICATION_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	//args := strings.Split(escapedPath, "/")
	//urlData := r.URL.Query()

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodGet:
		// Fetch requests
		data, err := globals.QueryJSON(globals.DB, "SELECT * FROM `requested_keys`", []interface{}{}, w)
		if err != nil {
			http.Error(w, "Error fetching requested keys from database.", http.StatusInternalServerError)
			return
		}

		// Write back
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(data)
		if err != nil {
			http.Error(w, "Error encoding keys.", http.StatusInternalServerError)
		}
		return

	// POST method
	case http.MethodPost:
		// Decode body
		var data []map[string]interface{}
		err := json.NewDecoder(r.Body).Decode(&data)
		if err != nil {
			http.Error(w, "Error decoding body", http.StatusUnprocessableEntity)
			return
		}

		// Fetch keys to move from body
		insertArgs := []interface{}{}
		deleteArgs := []interface{}{}
		var valuesQuery strings.Builder
		var whereQuery strings.Builder
		for _, kvp := range data {
			if keyvalue, ok := kvp["keyvalue"]; ok {
				valuesQuery.WriteString("(?), ")
				insertArgs = append(insertArgs, keyvalue)
				whereQuery.WriteString("requested_keys.keyvalue = ? OR ")
				deleteArgs = append(deleteArgs, keyvalue)
			}
		}

		// Verify that entries was actually added
		valuesQueryLen := len(valuesQuery.String())
		whereQueryLen := len(whereQuery.String())
		if valuesQueryLen <= 0 || whereQueryLen <= 0 {
			http.Error(w, "No valid keys provided.", http.StatusUnprocessableEntity)
			return
		}

		// Construct sql query
		sqlInsertQuery := fmt.Sprintf(
			"INSERT INTO `valid_keys` (`keyvalue`) VALUES %s",
			valuesQuery.String()[:valuesQueryLen-2],
		)

		sqlDeleteQuery := fmt.Sprintf(
			"DELETE FROM requested_keys WHERE %s",
			whereQuery.String()[:whereQueryLen-4],
		)

		// Execute
		res, err := globals.QueryJSON(globals.DB, sqlInsertQuery, insertArgs, w)
		if err != nil {
			fmt.Println("err: ", err)
			http.Error(w, "Error inserting keys into database.", http.StatusInternalServerError)
			return
		}

		_, err = globals.DB.Exec(sqlDeleteQuery, deleteArgs...)
		if err != nil {
			http.Error(w, "Error deleting keys from database.", http.StatusInternalServerError)
			return
		}

		// Write back
		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(res)
		if err != nil {
			http.Error(w, "Error encoding response.", http.StatusInternalServerError)
		}
		return

	// Other methods
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
}
