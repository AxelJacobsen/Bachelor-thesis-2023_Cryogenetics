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
const MOBILE_LOGIN_PATH = BASE_USER_PATH + "/login"    // "/api/user/login"
const WEB_LOGIN_PATH = BASE_USER_PATH + "/admin/login" // "/api/user/admin/login"
const WEB_PRIMARY_PATH = BASE_USER_PATH + "/admin"     // "/api/user/admin"
const WEB_ADMIN_PATH = BASE_PATH + "/getAdmins"        // "/api/getAdmins"
const SHARED_CREATE_PATH = BASE_PATH + "/create"       // "/api/create/container" or "/api/create/transactions" or "/api/create/employee"
const PUBLIC_STATUS_PATH = BASE_PATH + "/status"       // "/api/status"
const CRYPTOGRAPHY_PATH = BASE_PATH + "/cryptography"

// Database
//const DB_PATH = "mongodb://localhost:27017"

// Outgoing URLs

// File paths
const KEY_FILEPATH = "./keyfile.txt"
