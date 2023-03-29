package cryogenetics.logistics.api

import android.content.ContentValues.TAG
import android.os.StrictMode
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class Api {
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
    }
}