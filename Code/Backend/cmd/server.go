package main

import (
	"backend/constants"
	"backend/endpoints"
	"backend/globals"
	"backend/status"
	"context"
	"log"
	"net/http"
	"time"
)

/**
 *	The main function.
 * 	Starts the server.
 */
func main() {
	// Start session timer
	globals.StartTime = time.Now()
	// Context initialization
	globals.Ctx = context.Background()

	// Mongo initialization
	//globals.Client = mongo.InstansiateMongoClient(constants.DB_PATH)
	//mongo.ConnectContextToMongo(globals.Ctx, globals.Client)

	// disconnect afterwards
	//defer globals.Client.Disconnect(globals.Ctx)

	// Start listening to socket connections (clients)
	go endpoints.ListenForClients("0.0.0.0", "27015", "tcp")

	// Route
	routes := map[string]func(http.ResponseWriter, *http.Request){
		constants.MOBILE_LOGIN_PATH:       endpoints.HandlerMoblieLogin,
		constants.WEB_LOGIN_PATH:          endpoints.HandlerWebLogin,
		constants.WEB_PRIMARY_PATH:        endpoints.HandlerWebDashboard,
		constants.PUBLIC_TRANSACTION_PATH: endpoints.HandlerTransactions,
		constants.PUBLIC_INVENTORY_PATH:   endpoints.HandlerInventory,
		constants.PUBLIC_CLIENTS_PATH:     endpoints.HandlerClients,
		constants.PUBLIC_USERS_PATH:       endpoints.HandlerUsers,

		constants.PUBLIC_STATUS_PATH: status.HandlerStatus,
	}

	for route, routeTo := range routes {
		http.HandleFunc(route, routeTo)
		http.HandleFunc(route+"/", routeTo)
	}

	// Listen
	log.Println("Listening on port " + constants.PORT)
	log.Fatal(http.ListenAndServe(":"+constants.PORT, nil))
}
