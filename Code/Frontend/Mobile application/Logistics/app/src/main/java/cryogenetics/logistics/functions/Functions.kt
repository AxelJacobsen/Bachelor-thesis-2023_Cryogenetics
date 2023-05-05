package cryogenetics.logistics.functions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import cryogenetics.logistics.MainActivity
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Modifier
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import android.util.Base64

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

        /**
         *  Fetches the public key of the database.
         *
         *  @return The public key. If none was found, returns null.
         */
        fun fetchDBPublicKey() : PublicKey? {
            // Fetch from DB
            var data: String
            try {
                data = Api.fetchJsonData(ApiUrl.urlCryptography)
            } catch (e: Exception) {
                return null
            }

            // Parse
            val dataParsed = Api.parseJsonArray(data)
            if (dataParsed.isEmpty())
                return null

            // Convert to PublicKey object
            val keyFactory = KeyFactory.getInstance("RSA")
            val publicKeySpec = RSAPublicKeySpec (
                BigInteger(dataParsed[0]["N"] as String),
                BigInteger(dataParsed[0]["E"] as String)
            )
            return keyFactory.generatePublic(publicKeySpec)
        }

        /**
         *  Encrypts a set of bytes using a given public key.
         *
         *  @param bytes - The bytes to encrypt.
         *  @param publicKey - The public key to encrypt the bytes with.
         *
         *  @return The encrypted bytes.
         */
        fun encrypt(bytes: ByteArray, publicKey: ByteArray) : ByteArray {
            // Parse public key
            val publicKeySpec = X509EncodedKeySpec(publicKey)
            val publicKeyParsed = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)

            // Encrypt
            val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyParsed)

            return cipher.doFinal(bytes)
        }

        /**
         *  Decrypts a set of bytes.
         *
         *  @param bytes - The bytes to decrypt.
         *  @param privateKey - The private key to decrypt the bytes with. If none is given, uses ours.
         */
        suspend fun decrypt(context: Context, bytes: ByteArray, privateKey: PrivateKey? = null) : ByteArray? {
            var privateKeyFinal = privateKey

            // If no private key is given, fetch one
            if (privateKey == null)
                privateKeyFinal = fetchPrivateKey(context)
            if (privateKeyFinal == null)
                return null

            // Decrypt
            val cipher: Cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKeyFinal)

            return cipher.doFinal(bytes)
        }

        /**
         *  Fetches the keypair, generating one if none is found.
         *
         *  @param context - The context.
         *
         *  @return The keypair.
         */
        suspend fun fetchKeyPair(context: Context) : KeyPair {
            // Check data/preferences for existing keypair
            try {
                val fetchedPrivateKey: PrivateKey?  = fetchPrivateKey(context)
                val fetchedPublicKey: PublicKey?    = fetchPublicKey(context)
                if (fetchedPrivateKey != null && fetchedPublicKey != null)
                    return KeyPair(fetchedPublicKey, fetchedPrivateKey)
            } catch(e: Exception) {
                Log.d("Could not fetch keypair, creating new instead. Error: ", e.message ?: "none")
            }

            // Fetch keyPair and encode to string
            val keyPair = generateKeyPair(4096)
            val privateKeyStr = encodeBase64(keyPair.private.encoded)
            val publicKeyStr = encodeBase64(keyPair.public.encoded)

            // Write key strings to storage
            val privateKeyPrefKey = stringPreferencesKey("key_private_encoded")
            context.dataStore.edit {
                it[privateKeyPrefKey] = privateKeyStr
            }

            val publicKeyPrefKey = stringPreferencesKey("key_public_encoded")
            context.dataStore.edit {
                it[publicKeyPrefKey] = publicKeyStr
            }

            // Return
            return keyPair
        }

        /**
         *  Fetches the private key from storage.
         *
         *  @param context - The context.
         *
         *  @return The private key. If none was found, returns null.
         */
        suspend fun fetchPrivateKey(context: Context) : PrivateKey? {
            val key = stringPreferencesKey("key_private_encoded")
            val flow: Flow<String> = context.dataStore.data
                .map {
                    it[key] ?: ""
                }
            return runBlocking (Dispatchers.IO) {
                val privateKeyStr = flow.first()
                if (privateKeyStr == "")
                    return@runBlocking null

                val privateKeyBytes = decodeBase64(privateKeyStr)
                val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)
                return@runBlocking KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)
            }
        }

        /**
         *  Fetches the public key from storage.
         *
         *  @param context - The context.
         *
         *  @return The public key. If none was found, returns null.
         */
        suspend fun fetchPublicKey(context: Context) : PublicKey? {
            val key = stringPreferencesKey("key_public_encoded")
            val flow: Flow<String> = context.dataStore.data
                .map {
                    it[key] ?: ""
                }
            return runBlocking (Dispatchers.IO) {
                val publicKeyStr = flow.first()
                if (publicKeyStr == "")
                    return@runBlocking null

                val publicKeyBytes = decodeBase64(publicKeyStr)
                val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
                return@runBlocking KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)
            }
        }

        /**
         *  Generates a keyPair.
         *
         *  @param bits - Size of the key in bits.
         *
         *  @return The keypair.
         */
        fun generateKeyPair(bits: Int) : KeyPair {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(bits)
            return keyPairGenerator.generateKeyPair()
        }

        /**
         *  Encodes a set of bytes to string using base64.
         *  "\n" and "+" are replaced.
         *
         *  @param data - The data to encode.
         *
         *  @return The encoded data.
         */
        fun encodeBase64(data: ByteArray): String {
            val encoded = Base64.encodeToString(data, Base64.URL_SAFE)
            return encoded.replace("+", "{plus}").replace("\n", "{newline}")
        }

        /**
         *  Decodes a string to a set of bytes using base64.
         *  "{newline}" and "{plus}" are replaced.
         *
         *  @param encoded - The data to decode.
         *
         *  @return The decoded data.
         */
        fun decodeBase64(encoded: String): ByteArray {
            val encodedFixed = encoded.replace("{plus}", "+").replace("{newline}", "\n")
            return Base64.decode(encodedFixed, Base64.URL_SAFE)
        }

        /**
         *  Fetches the unique number of the device, generating one if none is found.
         *
         *  @param context - The context.
         *
         *  @return The unique number as a string.
         */
        suspend fun fetchUniqueNumber(context: Context) : String {
            // Fetch unique number from data/preferences
            val key = stringPreferencesKey("unique_number")
            val flow: Flow<String> = context.dataStore.data
                .map {
                    it[key] ?: ""
                }
            var uniqueNumber = runBlocking (Dispatchers.IO) {
                return@runBlocking flow.first()
            }
            if (uniqueNumber != "")
                return uniqueNumber

            // If unique number was not found, generate a new one and store it
            uniqueNumber = Random().nextInt(10000).toString()
            context.dataStore.edit {
                it[key] = uniqueNumber
            }

            return uniqueNumber
        }
    }
}