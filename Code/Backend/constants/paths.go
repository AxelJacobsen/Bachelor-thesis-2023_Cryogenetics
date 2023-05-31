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
* "/api/container_model"
* "/api/employee"
* "/api/location"
 */

// Endpoint paths
const WEB_PRIMARY_PATH = BASE_USER_PATH + "/admin" // "/api/user/admin"
const WEB_LOGIN_PATH = WEB_PRIMARY_PATH + "/login" // "/api/user/admin/login"
const ADMIN_VERIFICATION_PATH = WEB_PRIMARY_PATH + "/verification"

const SHARED_CREATE_PATH = BASE_PATH + "/create" // "/api/create/container" or "/api/create/transactions" or "/api/create/employee"
const CRYPTOGRAPHY_PATH = BASE_PATH + "/cryptography"
const MOBILE_VERIFICATION_PATH = BASE_USER_PATH + "/verification"

// File paths
const KEY_FILEPATH = "./keyfile.txt"

// Webapp paths
const WEBAPP_URL = "*"
