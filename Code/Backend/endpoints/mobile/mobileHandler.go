package mobile

import (
	paths "backend/constants"
	"backend/cryptography"
	"crypto/rsa"
	"encoding/json"
	"fmt"
	"math/big"
	"net/http"
	"strconv"
	"strings"
)

/**
 *	Handler for 'mobile login' endpoint.
 */
func HandlerMobileLogin(w http.ResponseWriter, r *http.Request) {
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
 *	Handler for 'verification' endpoint.
 */
func HandlerMobileVerification(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.SHARED_CREATE_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	//args := strings.Split(escapedPath, "/")
	//urlData := r.URL.Query()

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodPost:
		// Decode body
		var data []map[string]interface{}
		err := json.NewDecoder(r.Body).Decode(&data)
		if err != nil {
			fmt.Println("e0: ", err)
			http.Error(w, "Error decoding body", http.StatusUnprocessableEntity)
			return
		}

		if len(data) <= 0 {
			fmt.Println("e1: ", err)
			http.Error(w, "No data in body", http.StatusUnprocessableEntity)
			return
		}

		// Declare required vars
		uniqueNumberEncrypted := ""
		publicKeyE := ""
		publicKeyN := ""

		// Pick out required vars from body
		for k, v := range data[0] {
			switch k {
			case "unique_number":
				uniqueNumberEncrypted = fmt.Sprintf("%v", v)
			case "public_key_E":
				publicKeyE = fmt.Sprintf("%v", v)
			case "public_key_N":
				publicKeyN = fmt.Sprintf("%v", v)
			}
		}

		if uniqueNumberEncrypted == "" || publicKeyE == "" || publicKeyN == "" {
			http.Error(w, "Invalid query", http.StatusBadRequest)
			return
		}

		// Decode uniquenumber from base64
		uniqueNumberEncryptedBytes, err := cryptography.DecodeBase64(uniqueNumberEncrypted)
		if err != nil {
			fmt.Println("e4: ", err)
			http.Error(w, "Error decoding unique number from base64", http.StatusUnprocessableEntity)
			return
		}

		// Convert strings to (big)ints
		publicKeyEVal, err := strconv.Atoi(publicKeyE)
		if err != nil {
			fmt.Println("e2: ", err)
			http.Error(w, "Error parsing integer", http.StatusInternalServerError)
			return
		}
		publicKeyNVal := new(big.Int)
		publicKeyNVal.SetString(publicKeyN, 10)

		// Decrypt unique number
		uniqueNumberBytes, err := cryptography.Decrypt(uniqueNumberEncryptedBytes)
		if err != nil {
			fmt.Println("e3: ", err)
			http.Error(w, "Error decrypting unique number", http.StatusUnprocessableEntity)
			return
		}

		////////////////////////////////////////////////
		// ...verification on web frontend happens... //
		////////////////////////////////////////////////

		// Writeback
		res, err := cryptography.Encrypt(
			uniqueNumberBytes,
			&rsa.PublicKey{N: publicKeyNVal, E: publicKeyEVal},
		)
		if err != nil {
			fmt.Println("e5: ", err)
			http.Error(w, "Error encrypting data.", http.StatusInternalServerError)
			return
		}

		resb64 := cryptography.EncodeBase64(res)

		w.Header().Set("Content-Type", "application/json")
		err = json.NewEncoder(w).Encode(resb64)
		if err != nil {
			fmt.Println("e6: ", err)
			http.Error(w, "Error encoding data.", http.StatusInternalServerError)
		}

		return
	}
}
