package cryogenetics.logistics.api

class ApiCalls {
    companion object {
        fun fetchInventoryData(): List<Map<String, Any>> {
            val urlDataString = Api.fetchJsonData(ApiUrl.urlContainer)
            return Api.parseJsonArray(urlDataString)
        }
    }
}