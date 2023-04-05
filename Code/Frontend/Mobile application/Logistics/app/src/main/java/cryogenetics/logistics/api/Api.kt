package cryogenetics.logistics.api

import android.content.ContentValues.TAG
import android.os.StrictMode
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Api {
    val baseUrl = "http://10.0.2.2:8080/api/"
    companion object {
        /**
         * Gets json string from url
         *
         * Performs simple Get request to provided url and returns a string reading of the data
         *
         * @param urlString the complete url to query
         * @return the json data in string format, send directly to parseJson
         */
        fun fetchJsonData(urlString: String): String {
            //Update internet policy to perform request
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //Convert url string into url object
            val url = URL(urlString)
            //Open connection on the url string
            val connection = url.openConnection() as HttpURLConnection
            //Set connection method
            connection.requestMethod = "GET"
            //Perform API request
            connection.connect()

            val inputStream = connection.inputStream
            return inputStream.bufferedReader().use(BufferedReader::readText)
        }

        /**
         * Converts jsonString into a Map
         *
         * Takes the json string and puts it into a Map where the keys are equal to the json keys
         *
         * @param jsonText the direct result of @fetchJsonData
         * @return a List of <Map<String,Any>> containing json data
         */
        fun parseJsonArray(jsonText: String): List<Map<String, Any>> {
            // convert the jsonText into a jsonArray
            val jsonArray = JSONArray(jsonText)
            // initialize the map list
            val itemList = mutableListOf<Map<String, Any>>()

            //Loop the length of jsonArray
            for (i in 0 until jsonArray.length()) {
                //Fetch the object at current "i" position
                val jsonObject = jsonArray.getJSONObject(i)
                //Prepare a map to contain it
                val itemMap = mutableMapOf<String, Any>()

                if (jsonObject != null) {
                    //fill the map with data from the object
                    for (key in jsonObject.keys()) {
                        itemMap[key] = jsonObject.get(key)
                    }
                } else {
                    Log.e(TAG, "Received no json data")
                }
                //Add the object to the map list
                itemList.add(itemMap)
            }
            return itemList
        }
        /// IN PROGRESS --- Function works as intended however returns 404 when connecting to server
        /// Might look at it at the cottage - Axel
        fun makeBackendRequest(endpoint: String, dataList: List<Map<String, Any>>, method: String) {
            // Define the base URL for your backend server
            val baseUrl = "http://10.0.2.2:8080/api/"

            // Construct the URL for the endpoint you want to hit
            val endpointUrl = URL(baseUrl + endpoint)

            var jsonString = generateJson(dataList)
            println("HERE!")
            if (jsonString.isNotEmpty()) {
                println(jsonString)
            }
            // Open a connection to the endpoint URL
            val connection: HttpURLConnection = endpointUrl.openConnection() as HttpURLConnection

            // Set the request method
            connection.requestMethod = method

            // Set the request headers
            connection.setRequestProperty("Content-Type", "application/json")

            // Send the request body
            val outputStreamWriter = OutputStreamWriter(connection.outputStream)
            outputStreamWriter.write(jsonString)
            outputStreamWriter.flush()

            // Get the response code
            val responseCode = connection.responseCode
            Log.i(TAG,responseCode.toString())

            // Close the connection and output stream writer
            outputStreamWriter.close()
            connection.disconnect()
        }


        fun generateJson(dataList: List<Map<String, Any>>): String {
            val jsonArray = StringBuilder("[")
            for ((index, data) in dataList.withIndex()) {
                if (index > 0) jsonArray.append(",")
                jsonArray.append("{")
                for ((i, entry) in data.entries.withIndex()) {
                    if (i > 0) jsonArray.append(",")
                    jsonArray.append("\"${entry.key}\":")
                    when (val value = entry.value) {
                        is String -> jsonArray.append("\"$value\"")
                        null -> jsonArray.append("null")
                        else -> jsonArray.append(value)
                    }
                }
                jsonArray.append("}")
            }
            jsonArray.append("]")
            return jsonArray.toString()
        }
    }
}