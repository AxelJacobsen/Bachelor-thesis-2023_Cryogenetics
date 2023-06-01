package cryogenetics.logistics.api

class ApiCalls {
    companion object {
        /**
         * Fetches data from the backend, and returns an inventory table.
         * @return current inventory table
         */
        fun fetchInventoryData(): List<Map<String, Any>> {
            val urlDataString = Api.fetchJsonData(ApiUrl.urlContainer)
            return Api.parseJsonArray(urlDataString)
        }

        /**
         * Fetches data from the backend, and returns an actlog table.
         * @return current act log table
         */
        fun fetchActLogData() :  List<Map<String, Any>>{
            val urlDataString = Api.fetchJsonData(ApiUrl.urlTransaction)
            return Api.parseJsonArray(urlDataString)
        }

        fun fetchContainerModel() :  List<Map<String, Any>>{
            val urlDataString = Api.fetchJsonData(ApiUrl.urlContainerModel)
            return Api.parseJsonArray(urlDataString)

        }
    }
}