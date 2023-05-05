package cryogenetics.logistics.functions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cryogenetics.logistics.MainActivity
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Modifier
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

        fun getAlias(context: Context) : String {
            var tvUsername = ""
            // Set username text
            val key = stringPreferencesKey("employee_alias")
            val flow: Flow<String> = context.dataStore.data
                .map {
                    it[key] ?: "No name found"
                }
            runBlocking(Dispatchers.IO) {
                return@runBlocking flow.first()
            }
            return ""
        }

        fun getDateTime(): String? {
            val myFormat = "yyyy-MM-dd HH:mm:ss"
            val dateFormat = SimpleDateFormat(myFormat, Locale.US)
            val date = Date()
            return dateFormat.format(date)
        }
        /**
         *  Gets the value of a string resource by its name.
         *
         *  @param name - The "name" of the string resource, i.e. "app_name".
         *
         *  @return The value of the string resource with the given name, i.e. "Logistics".
         */
        fun GetStringByName(name: String, context: Context) : String {
            val rID = context.resources.getIdentifier(name, "string", context.packageName)
            return context.getString(rID)
        }

        /**
         *  Gets the name of a string resource by its value.
         *
         *  @param value - The string resource's value, i.e. "Logistics"
         *
         *  @return The name of the string resource with the given value, i.e. "app_name".
         */
        fun GetStringByValue(value: String, context: Context) : String {
            // Fetch and iterate all string resources ("fields")
            val allStrings = R.string::class.java.declaredFields
            for (str in allStrings) {
                // Verify that the field is...
                if (
                    Modifier.isStatic(str.modifiers) &&     // Static
                    !Modifier.isPrivate(str.modifiers) &&   // Not private
                    str.type == Int::class.java             // Of type Int (resourceID)
                ) {
                    try {
                        // If the correct string resource is found, return it
                        if (value == context.getString(str.getInt(null)))
                            return str.name
                    } catch (e: Exception) { continue }
                }
            }

            // Otherwise, if no correlating string resource was found, return ""
            return ""
        }

        /**
         *  Gets the columns of a given table from the database.
         *
         *  @param table - The table.
         *
         *  @return All columns associated with the given table in the format (name, type, keytype).
         */
        private fun getColumns(table: String) : List<Triple<String,String,String>> {
            var jsonRaw: String
            try {
                jsonRaw = Api.fetchJsonData("http://10.0.2.2:8080/api/$table/columns")
            } catch (e: Exception) {
                return emptyList()
            }
            val jsonParsed = Api.parseJsonArray(jsonRaw)
            return jsonParsed.map { Triple(
                it["COLUMN_NAME"].toString(),
                it["COLUMN_TYPE"].toString(),
                it["COLUMN_KEY"].toString()
            ) }
        }

        /**
         *  Stores a map to data/preferences.
         *
         *  @param m - The map to store.
         */
        suspend fun storeMap(m: Map<String, Any>, context: Context) {
            for (kvp in m) {
                when (kvp.value) {
                    is String -> {
                        val key = stringPreferencesKey(kvp.key)
                        context.dataStore.edit {
                            it[key] = kvp.value as String
                        }
                    }

                    is Int -> {
                        val key = intPreferencesKey(kvp.key)
                        context.dataStore.edit {
                            it[key] = kvp.value as Int
                        }
                    }
                }
            }
        }

        /**
         *  Restarts the app.
         */
        fun restartApp(context: Context, nextIntent: Intent?) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
            Runtime.getRuntime().exit(0)
        }
    }
}