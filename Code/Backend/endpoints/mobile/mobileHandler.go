package mobile

import (
	paths "backend/constants"
	"backend/cryptography"
	"backend/globals"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
)

/**
 *	Handler for 'verification' endpoint.
 */
func HandlerMobileVerification(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

	// Get escaped path without base URL and remove the first character if it's a "/"
	escapedPath := r.URL.EscapedPath()[len(paths.MOBILE_VERIFICATION_PATH):]

	if len(escapedPath) > 0 && escapedPath[0] == '/' {
		escapedPath = escapedPath[1:]
	}

	args := strings.Split(escapedPath, "/")
	//urlData := r.URL.Query()

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

		if len(data) <= 0 {
			http.Error(w, "No data in body", http.StatusUnprocessableEntity)
			return
		}

		// If we're just checking if the number is already verified...
		if len(args) > 0 && args[0] == "check" {
			if cryptography.VerifyUniqueNumber(globals.DB, string(uniqueNumberBytes)) {
				// Writeback an OK message
				w.Header().Set("Content-Type", "application/json")
				err = json.NewEncoder(w).Encode("Key recognized")
				if err != nil {
					http.Error(w, "Error encoding OK message.", http.StatusInternalServerError)
				}
			} else {
				http.Error(w, "Invalid unique number.", http.StatusUnprocessableEntity)
			}
			return

		} else {
			// Request verification
			err := cryptography.RequestVerification(globals.DB, string(uniqueNumberBytes))
			if err != nil {
				http.Error(w, "Error requesting access", http.StatusInternalServerError)
			}
			return
		}

	// Other methods
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
}
