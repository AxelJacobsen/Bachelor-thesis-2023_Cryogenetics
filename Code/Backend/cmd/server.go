package main

import (
	"fmt"
	"net/http"
	"os"

	_ "github.com/go-sql-driver/mysql"
)

////////////////////////////////////////////////////
/// USING https://github.com/go-sql-driver/mysql ///
////////////////////////////////////////////////////

func main() {
	// Get port
	port := os.Getenv("HTTP_PLATFORM_PORT")
	if port == "" {
		port = "8080"
	}

	http.HandleFunc("/", HelloServer)
	http.ListenAndServe(":"+port, nil)
}

/**
 *	A simple handler
 */
func HelloServer(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "Current endpoint!, %s!", r.URL.Path[1:])
}

/**
 *	The main function.
 * 	Starts the server.
 *
func main() {
	// Connect to database
	db, err := sql.Open("mysql", "root@tcp(127.0.0.1:3306)/cryogenetics_database")
	if err != nil {
		panic(err.Error())
	}
	defer db.Close()
	globals.DB = db

	// Start session timer
	globals.StartTime = time.Now()
	// Context initialization
	globals.Ctx = context.Background()

	// Route
	routes := map[string]func(http.ResponseWriter, *http.Request){
		constants.BASE_PATH:                shared.EndpointHandler,
		constants.SHARED_CREATE_PATH:       shared.CreateDataHandler,
		constants.WEB_LOGIN_PATH:           web.HandlerWebLogin,
		constants.CRYPTOGRAPHY_PATH:        shared.CryptographyHandler,
		constants.MOBILE_VERIFICATION_PATH: mobile.HandlerMobileVerification,
		constants.ADMIN_VERIFICATION_PATH:  web.HandlerVerification,
	}

	for route, routeTo := range routes {
		http.HandleFunc(route, routeTo)
		http.HandleFunc(route+"/", routeTo)
	}

	// Listen
	log.Println("Listening on port " + constants.PORT)
	log.Fatal(http.ListenAndServe(":"+constants.PORT, nil))
}*/
