package main

import (
	"backend/constants"
	"backend/endpoints/mobile"
	"backend/endpoints/shared"
	"backend/endpoints/web"
	"backend/globals"
	"context"
	"database/sql"
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
	// Connect to database
	db, err := sql.Open("mysql", "azure:6#vWHD_$@tcp(127.0.0.1)/cryogenetics_database")
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
	port := os.Getenv("HTTP_PLATFORM_PORT")
	if port == "" {
		port = constants.PORT
	}
	log.Println("Listening on port " + port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
