package cryogenetics.logistics.functions

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.widget.Toast
import java.util.*

class Functions {
    companion object {
        /**
         * Updates Temp number to also carry container size
         *
         * Iterates data provided and returns an updated List containing corrected number
         *
         * @param urlString the complete url to query
         * @return the json data in string format, send directly to parseJson
         */
        fun enforceNumberFormat(inData: Map<String, Any>): Map<String, Any> {
            var editMap = mutableMapOf<String,Any>()
            editMap = inData as MutableMap<String, Any>
            val fieldNames = listOf<String>("id", "liter_capacity")
            editMap[fieldNames[0]] =
                    (inData[fieldNames[1]].toString() +
                    "-" +
                    addZeros(inData[fieldNames[0]].toString()))
            return editMap
        }

        /**
         * Adds zeros in front of temp number to enforce uniformity
         *
         * Pretty dumb and inefficient but it gets the job done by checking if length of number is
         * smaller than the desired number of characters, if not adds more zeros in front
         *
         * @param inString id from json data
         * @return id but withe more zeros
         */
        fun addZeros(inString: String): String{
            val totalChars = 4
            var outString = ""
            if (inString.length < totalChars){
                var i = 0
                while (i < (totalChars-inString.length)){
                    outString += "0"
                    i++
                }
                outString += inString
            } else {
                return inString
            }
            return outString
        }

        fun searchContainer(
            context: Context,
            fetchedData: List<Map<String, Any>>,
            searchValue: String = ""
        ): MutableList<Map<String, Any>> {
            val searchResults = mutableListOf<Map<String, Any>>()
            if (fetchedData.isNotEmpty() && searchValue != ""){
                for (model in fetchedData) {
                    for (value in model.values) {
                        if (value.toString().contains(searchValue)) {
                            searchResults.add(model)
                            break
                        }
                    }
                }
            } else {
                Toast.makeText(context, "No search value entered, or no Tanks added!", Toast.LENGTH_LONG).show()
            }
            return searchResults
        }

        fun getDate(): String? {
            val myFormat = "yyyy-MM-dd"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            val date = Date()
            return dateFormat.format(date)
        }

        fun getDateTime(): String? {
            val myFormat = "yyyy-MM-dd HH:mm:ss"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            val date = Date()
            return dateFormat.format(date)
        }
    }
}