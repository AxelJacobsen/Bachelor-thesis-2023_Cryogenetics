package cryogenetics.logistics.api

object ApiUrl {
    const val urlBase : String = "http://10.0.2.2:8080/api/"

    const val urlLocation = "${urlBase}location"
    const val urlClient : String = "${urlBase}client"
    const val urlAct : String = "${urlBase}act"
    const val urlStatus : String = "${urlBase}container_status"
    const val urlTransaction : String = "${urlBase}transaction"
    const val urlContainer : String = "${urlBase}container"
}