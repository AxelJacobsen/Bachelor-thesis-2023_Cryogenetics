package status

import (
	"net/http"
)

/**
 *	Handler for 'status' endpoint.
 */
func HandlerStatus(w http.ResponseWriter, r *http.Request) {
	w.Header().Add("content-type", "application/json")

	// Switch based on method
	switch r.Method {

	// GET method
	case http.MethodGet:
		return

	// Other method
	default:
		http.Error(w, "Method not allowed, read the documentation for more information.", http.StatusMethodNotAllowed)
		return
	}
}
