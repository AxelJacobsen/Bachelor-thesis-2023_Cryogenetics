package cryogenetics.logistics.api

class ApiCalls {
    companion object {
        fun fetchInventoryData(): List<Map<String, Any>> {
            val urlDataString = Api.fetchJsonData(ApiUrl.urlContainer)
            return Api.parseJsonArray(urlDataString)
        }
        fun fetchActLogData() :  List<Map<String, Any>>{
            val urlDataString = Api.fetchJsonData(ApiUrl.urlTransaction)
            return Api.parseJsonArray(urlDataString)
        }
    }
}