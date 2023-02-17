package constants

// Port
const PORT = "8080"

//Path components
const BASE_PATH = "/api"
const USER_PATH = "/user"
const BASE_USER_PATH = BASE_PATH + USER_PATH

// Endpoint paths
const MOBILE_LOGIN_PATH = BASE_USER_PATH + "/login"         // "/api/user/login"
const WEB_LOGIN_PATH = BASE_USER_PATH + "/admin/login"      // "/api/user/admin/login"
const WEB_PRIMARY_PATH = BASE_USER_PATH + "/admin"          // "/api/user/admin"
const PUBLIC_TRANSACTION_PATH = BASE_PATH + "/transactions" // "/api/transactions"
const PUBLIC_INVENTORY_PATH = BASE_PATH + "/inventory"      // "/api/inventory"
const PUBLIC_CLIENTS_PATH = BASE_PATH + "/client"           // "/api/client"
const PUBLIC_USERS_PATH = BASE_USER_PATH + "/users"         // "/api/user/login"

const PUBLIC_STATUS_PATH = BASE_PATH + "/status" // "/api/status"

// Database
//const DB_PATH = "mongodb://localhost:27017"

// Outgoing URLs
