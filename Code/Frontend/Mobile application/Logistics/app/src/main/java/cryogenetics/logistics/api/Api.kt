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
        /*
        * Performs simple Get request to provided url and returns a string reading of the data
        * */
        fun fetchJsonData(urlString: String): String {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream
            return inputStream.bufferedReader().use(BufferedReader::readText)
        }

        fun parseJsonArray(jsonText: String): List<Map<String, Any>> {
            val jsonArray = JSONArray(jsonText)
            val itemList = mutableListOf<Map<String, Any>>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val itemMap = mutableMapOf<String, Any>()

                if (jsonObject != null) {
                    for (key in jsonObject.keys()) {
                        itemMap[key] = jsonObject.get(key)
                    }
                } else {
                    Log.e(TAG, "Received no json data")
                }

                itemList.add(itemMap)
            }
            return itemList
        }
    }
}