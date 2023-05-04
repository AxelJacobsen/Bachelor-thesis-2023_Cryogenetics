package cryogenetics.logistics.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cryogenetics.logistics.MainActivity
import android.icu.text.SimpleDateFormat
import android.widget.Toast
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.dataStore
import kotlinx.coroutines.delay
import java.lang.reflect.Modifier
import java.util.*


class Util {
    companion object {
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
            delay(2000) // Fake loading time. TODO: Remove this line
        }

        /**
         *  Restarts the app.
         */
        fun restartApp(context: Context, nextIntent: Intent?) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
            Runtime.getRuntime().exit(0)
        }

    }
}