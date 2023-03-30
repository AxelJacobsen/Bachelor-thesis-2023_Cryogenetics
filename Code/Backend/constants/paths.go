package constants

// Port
const PORT = "8080"

//Path components
const BASE_PATH = "/api"
const USER_PATH = "/user"
const BASE_USER_PATH = BASE_PATH + USER_PATH

//UPDATED VERSION:
/**
* "/api/transaction"
* "/api/container"
* "/api/client"
* "/api/act"
* "/api/container_status"
* "/api/container_model"
* "/api/employee"
* "/api/location"
 */

// Endpoint paths
const MOBILE_LOGIN_PATH = BASE_USER_PATH + "/login"         // "/api/user/login"
const WEB_LOGIN_PATH = BASE_USER_PATH + "/admin/login"      // "/api/user/admin/login"
const WEB_PRIMARY_PATH = BASE_USER_PATH + "/admin"          // "/api/user/admin"
const WEB_ADMIN_PATH = BASE_PATH + "/getAdmins"             // "/api/getAdmins"
const PUBLIC_TRANSACTION_PATH = BASE_PATH + "/transactions" // "/api/transactions"
const PUBLIC_CLIENTS_PATH = BASE_PATH + "/client"           // "/api/client"
const PUBLIC_USERS_PATH = BASE_USER_PATH + "/users"         // "/api/user/login" Actually: "/api/user/users"
const PUBLIC_CONTAINER_PATH = BASE_USER_PATH + "/container" // "/api/user/container"
const PUBLIC_ACT_PATH = BASE_USER_PATH + "/act"             // "/api/user/act"

const PUBLIC_STATUS_PATH = BASE_PATH + "/status"     // "/api/status"
const PUBLIC_MODEL_PATH = BASE_PATH + "/model"       // "/api/model"
const PUBLIC_LOCATION_PATH = BASE_PATH + "/location" // "/api/location"

// Database
//const DB_PATH = "mongodb://localhost:27017"

// Outgoing URLs
