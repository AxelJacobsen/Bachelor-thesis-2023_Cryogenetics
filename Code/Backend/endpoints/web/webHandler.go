package web

import (
	paths "backend/constants"
	"backend/globals"
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
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
