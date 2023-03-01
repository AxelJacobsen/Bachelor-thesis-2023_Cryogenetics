package main

import (
	"backend/constants"
	"backend/endpoints/mobile"
	"backend/endpoints/shared"
	"backend/endpoints/web"
	"backend/globals"
	"backend/status"
	"context"
	"log"
	"net/http"
	"time"

	"database/sql"

	_ "github.com/go-sql-driver/mysql"
)

////////////////////////////////////////////////////
/// USING https://github.com/go-sql-driver/mysql ///
////////////////////////////////////////////////////

/*
Structs:
	Admin
	Client
	Container
	ContainerModel
	ContainerStatus
	Employee
	Location
	Transaction
	TransactionAction

Functions:
	getAdmins(SC) ?
	addAdmin(Admin)
	getClients(SC)
	addClient(Client)
	getContainer(SC)
	addContainer(Container)
	getContainerModels()
	addContainerModel(ContainerModel)
	getContainerStatuses()
	addContainerStatus(ContainerStatus)
	getEmployees(SC)
	addEmployee(Employee)
	getLocations(SC)
	addLocation(Location)
	getTransactions(SC)
	addTransaction(Transaction)
	getTransactionActions()
	addTransactionAction(TransactionAction)

Glossary:
	SC = Search Criteria
*/

/**
 *	The main function.
 * 	Starts the server.
 */
func main() {

	// Connect to database
	db, err := sql.Open("mysql", "root@tcp(127.0.0.1:3306)/database")
	if err != nil {
		panic(err.Error())
	}
	defer db.Close()
	globals.DB = db

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
	go globals.ListenForClients("0.0.0.0", "27015", "tcp")

	// Route
	routes := map[string]func(http.ResponseWriter, *http.Request){
		constants.MOBILE_LOGIN_PATH:       mobile.HandlerMoblieLogin,
		constants.WEB_LOGIN_PATH:          web.HandlerWebLogin,
		constants.WEB_PRIMARY_PATH:        web.HandlerWebDashboard,
		constants.PUBLIC_TRANSACTION_PATH: shared.HandlerTransactions,
		constants.PUBLIC_INVENTORY_PATH:   shared.HandlerInventory,
		constants.PUBLIC_CLIENTS_PATH:     shared.HandlerClients,
		constants.PUBLIC_USERS_PATH:       shared.HandlerUsers,

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
