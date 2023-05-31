package cryogenetics.logistics.api

object ApiUrl {
    const val urlBase : String = "https://cryogenetics-logistics-solution.azurewebsites.net/api/"

    const val urlLocation = "${urlBase}location"
    const val urlClient : String = "${urlBase}client"
    const val urlAct : String = "${urlBase}act"
    const val urlStatus : String = "${urlBase}container_status"
    const val urlTransaction : String = "${urlBase}transaction"
    const val urlContainer : String = "${urlBase}container"
    const val urlEmployee : String = "${urlBase}employee"
    const val urlCryptography : String = "${urlBase}cryptography"
    const val urlContainerModel : String = "${urlBase}container_model"
}