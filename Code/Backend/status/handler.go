package status

import (
	"net/http"
)

/**
 *	Handler for 'status' endpoint.
 */
func HandlerStatus(w http.ResponseWriter, r *http.Request) {
	if r.Method == "GET" {
		w.Write([]byte("this is temporary, but it's working"))
	} else {
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Write([]byte("405 - Method Not Allowed"))
	}
}
