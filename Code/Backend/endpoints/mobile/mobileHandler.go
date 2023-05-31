package mobile

import (
	"backend/constants"
	"backend/cryptography"
	"backend/globals"
	"encoding/json"
	"fmt"
	"net/http"
)

/**
 *	Handler for 'verification' endpoint.
 */
func HandlerMobileVerification(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")
	w.Header().Set("Access-Control-Allow-Origin", constants.WEBAPP_URL)

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodPost:
		// Decode body
		var data []map[string]interface{}
		err := json.NewDecoder(r.Body).Decode(&data)
		if err != nil {
			http.Error(w, "Error decoding body", http.StatusUnprocessableEntity)
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
			http.Error(w, "Error decoding unique number from base64", http.StatusUnprocessableEntity)
			return
		}

		// Decrypt unique number
		uniqueNumberBytes, err := cryptography.Decrypt(uniqueNumberEncryptedBytes)
		if err != nil {
			http.Error(w, "Error decrypting unique number", http.StatusUnprocessableEntity)
			return
		}

		// If no data was given, return an error
		if len(data) <= 0 {
			http.Error(w, "No data in body", http.StatusUnprocessableEntity)
			return
		}

		// Check if the number has already been verified
		if cryptography.VerifyUniqueNumber(globals.DB, string(uniqueNumberBytes)) {
			// Writeback an OK message
			w.Header().Set("Content-Type", "application/json")
			err = json.NewEncoder(w).Encode("Key recognized")
			if err != nil {
				http.Error(w, "Error encoding OK message.", http.StatusInternalServerError)
			}
			return
		}

		// If it has not, request verification
		if cryptography.RequestVerification(globals.DB, string(uniqueNumberBytes)) != nil {
			http.Error(w, "Error requesting access", http.StatusInternalServerError)
			return
		}
		http.Error(w, "Access requested.", http.StatusUnprocessableEntity)
		return

	// Other methods
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
}
