package main

import (
	"backend/constants"
	"backend/endpoints/mobile"
	"backend/endpoints/shared"
	"backend/endpoints/web"
	"backend/globals"
	"context"
	"database/sql"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"

	_ "github.com/go-sql-driver/mysql"
)

////////////////////////////////////////////////////
/// USING https://github.com/go-sql-driver/mysql ///
////////////////////////////////////////////////////

/**
 *	The main function.
 * 	Starts the server.
 */
func main() {
	// Start session timer
	globals.StartTime = time.Now()

	// Connect to database
	logged_in := false
	for !logged_in {
		// Check for timeout
		if time.Since(globals.StartTime).Seconds() > constants.DB_TIMEOUT {
			panic(errors.New("timeout while attempting to connect to database"))
		}

		// Try connecting to DB
		db, err := sql.Open("mysql", fmt.Sprintf("%s:%s@tcp(%s)/%s", constants.DB_USER, constants.DB_PSW, constants.DB_CONN, constants.DB_NAME))
		if err != nil {
			fmt.Println("Error connecting: ", err)
			continue
		}
		defer db.Close()

		// Try pinging
		err = db.Ping()
		if err != nil {
			fmt.Println("Error pinging: ", err)
			continue
		}

		// Logged in
		logged_in = true
		globals.DB = db
	}

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
	port := os.Getenv("HTTP_PLATFORM_PORT")
	if port == "" {
		port = constants.PORT
	}
	log.Println("Listening on port " + port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
