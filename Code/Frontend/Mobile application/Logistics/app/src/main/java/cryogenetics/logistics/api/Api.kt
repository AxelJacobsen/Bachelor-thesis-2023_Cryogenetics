package cryogenetics.logistics.api

import android.content.ContentValues.TAG
import android.os.StrictMode
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
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

            val respCode = connection.responseCode
            if (respCode != 200) {
                Log.e(TAG, "Error in feching data, returned code: $respCode")
                return ""
            }

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
            var jsonArray: JSONArray
            try {
                jsonArray = JSONArray(jsonText)
            } catch (e: JSONException) {
                Log.e(TAG, "Error parsing jsonText as jsonArray")
                return mutableListOf<Map<String, Any>>()
            }
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

        /**
         * Performs a PUT or POST request to backend
         *
         * Takes the endpoint to contact which handles the table in the backend, sends json data in
         * the form of a List<Map<String, Any>> where the key is the json table name
         *
         * @param endpoint String representation of the backend endpoint to send request to
         * @param dataList List of json data in Map format
         * @param method Request method
         */
        fun makeBackendRequest(endpoint: String, dataList: List<Map<String, Any>>, method: String) {
            //Lists legal methods, can be expanded on if more methods following the same format are
            // accommodated for.
            val legalMethods = listOf<String>("POST", "PUT")

            // Define the base URL for your backend server
            val baseUrl = "http://10.0.2.2:8080/api/"

            //Check if provided method is allowed
            if (!legalMethods.contains(method.uppercase())){
                Log.e(TAG, "Illegal method in API call")
            }

            // Construct the URL for the endpoint you want to hit
            val endpointUrl = URL(baseUrl + endpoint)

            //Turn the Map into a json string
            var jsonString = generateJson(dataList)
            if (jsonString.isEmpty()){
                Log.e(TAG, "Couldn't construct json string")
            }

            // Open a connection to the endpoint URL
            val connection: HttpURLConnection = endpointUrl.openConnection() as HttpURLConnection

            // Set the request method
            connection.requestMethod = method.uppercase()

            // Set the request headers
            connection.setRequestProperty("Content-Type", "application/json")

            // Send the request body
            val outputStreamWriter = OutputStreamWriter(connection.outputStream)
            outputStreamWriter.write(jsonString)
            outputStreamWriter.flush()

            // Close the connection and output stream writer
            outputStreamWriter.close()
            connection.disconnect()
        }

        /**
         * Converts List<Map<String, Any>> to jsonstring
         *
         * Turns a List<Map<String, Any>> into json where the key is the json table name
         *
         * @param dataList List of data to be converted
         * @return a json string with converted data
         */
        fun generateJson(dataList: List<Map<String, Any>>): String {
            if (dataList.isEmpty()){
                //Map is empty,
                return ""
            }
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