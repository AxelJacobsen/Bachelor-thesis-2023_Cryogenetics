package cryogenetics.logistics.util

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.widget.Toast
import cryogenetics.logistics.R
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
    }
}