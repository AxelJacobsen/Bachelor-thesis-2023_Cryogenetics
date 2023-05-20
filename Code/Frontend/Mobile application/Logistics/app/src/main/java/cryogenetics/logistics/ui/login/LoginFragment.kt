package cryogenetics.logistics.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.datastore.preferences.core.*
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.dataStore
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.ui.host.HostFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import android.util.Base64
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.nio.charset.StandardCharsets

/**
 *  Possible errors from attempting to log in.
 */
enum class LoginResponse {
    Success, FormatError, LoginError
}

/**
 *  Login fragment.
 */
class LoginFragment : Fragment() {
    // Components
    private lateinit var bLogin: AppCompatButton
    private lateinit var etLogin: EditText
    private lateinit var pbLogin: ProgressBar
    private lateinit var verifyPopup: ConstraintLayout
    private lateinit var afterVerify: ConstraintLayout
    private lateinit var tvLoginUniqueNumber: TextView
    private lateinit var bLoginVerify: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: DELETE THIS WHEN DONE TESTING
        /*GlobalScope.launch(Unconfined) {
            val preferenceKey = stringPreferencesKey("key_verified")
            requireContext().dataStore.edit {
                it[preferenceKey] = ""
            }
        }*/

        // Fetch components
        bLogin = view.findViewById(R.id.bLogin)
        etLogin = view.findViewById(R.id.etLogin)
        pbLogin = view.findViewById(R.id.pbLogin)
        verifyPopup = view.findViewById(R.id.verifyPopup)
        afterVerify = view.findViewById(R.id.afterVerify)
        tvLoginUniqueNumber = view.findViewById(R.id.tvLoginUniqueNumber)
        bLoginVerify = view.findViewById(R.id.bLoginVerify)

        // Check if the device is verified
        //verifyPopup.visibility = View.VISIBLE
        //afterVerify.visibility = View.GONE
        bLoginVerify.setOnClickListener {
            GlobalScope.launch(Unconfined) {
                tvLoginUniqueNumber.text = Functions.fetchUniqueNumber(requireContext())
                val key = verifyDevice()
                runBlocking {
                    if (key != null) {
                        requireActivity().runOnUiThread {
                            verifyPopup.visibility = View.GONE
                            afterVerify.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        bLoginVerify.performClick()

        // Set listeners
        bLogin.setOnClickListener {
            val idStr = etLogin.text.toString()
            val loginAttempt = attemptLogin(idStr)
            when (loginAttempt.first) {
                // On success, save the employee information and continue
                LoginResponse.Success -> {
                    GlobalScope.launch(Unconfined) {
                        // Begin storing information and display a loading icon
                        requireActivity().runOnUiThread {
                            bLogin.visibility = View.GONE
                            pbLogin.visibility = View.VISIBLE
                        }
                        Functions.storeMap(loginAttempt.second[0], requireContext())

                        runBlocking {
                            // Once the storing is complete, exit login screen and enter HostFragment
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.activityMain, HostFragment.newInstance())
                                .commit()
                        }
                    }
                }

                // On format error, return a fitting error message
                LoginResponse.FormatError -> {
                    Toast.makeText(requireContext(), getString(R.string.login_id_formatError_message), Toast.LENGTH_SHORT).show()
                }

                // On login error, return a fitting error message
                LoginResponse.LoginError -> {
                    Toast.makeText(requireContext(), getString(R.string.login_id_loginError_message), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    /**
     *  Validates an id's format.
     *
     *  @param id - The id.
     *
     *  @return True if the id is valid, false otherwise.
     */
    private fun validateFormat(idStr: String) : Boolean {
        if (
            idStr.length < 3 ||                                     // Verify length
            idStr.filter { it.isDigit() }.length != idStr.length    // Verify symbols
        )
            return false
        return true
    }

    /**
     *  Fetches an employee by their ID.
     *
     *  @param idStr - The ID as a string.
     *
     *  @return The employee information. If the employee was not found, returns an empty list instead.
     */
    private fun fetchEmployeeByID(idStr: String) : List<Map<String, Any>> {
        // Fetch the given employee from db
        val url = "${ApiUrl.urlEmployee}/?login_code=$idStr"
        val data = Api.fetchJsonData(url)
        return Api.parseJsonArray(data)
    }

    /**
     *  Attempt to log in with an ID.
     *
     *  @param idStr - The ID as a string.
     *
     *  @return A pair containing the login response and employee id (if success).
     */
    private fun attemptLogin(idStr: String) : Pair<LoginResponse, List<Map<String, Any>>> {
        // Invalid format
        if (!validateFormat(idStr))
            return Pair(LoginResponse.FormatError, emptyList())

        // Attempt to fetch employee from DB
        val employeeData = fetchEmployeeByID(idStr)
        if (employeeData.isEmpty())
            return Pair(LoginResponse.LoginError, emptyList())

        // Return
        return Pair(LoginResponse.Success, employeeData)
    }

    /**
     *  Verifies the device.
     *  If it has been verified before, returns the stored key. Otherwise, requests a new one.
     *
     *  @return The verification key.
     */
    private suspend fun verifyDevice() : String? {
        // Check if a key is already stored
        val preferenceKey = stringPreferencesKey("key_verified")
        val flow: Flow<String> = requireContext().dataStore.data
            .map {
                it[preferenceKey] ?: ""
            }
        var key: String? = runBlocking (Dispatchers.IO) {
            val keyFetched = flow.first()
            if (keyFetched == "")
                return@runBlocking null
            return@runBlocking keyFetched
        }

        // Fetch DB public key
        val publicKeyDB = Functions.fetchDBPublicKey()
        if (publicKeyDB == null) {
            Log.e("Database error", "Could not fetch database public key")
            return null
        }

        // Fetch our own public key
        val mKeyPair = Functions.fetchKeyPair(requireContext())
        val n = (mKeyPair.public as RSAPublicKey).modulus
        val e = (mKeyPair.public as RSAPublicKey).publicExponent

        // If key was found earlier, check it
        if (key != null) {
            val uniqueEncryptedBytes = Functions.encrypt(key.toByteArray(), publicKeyDB.encoded)
            val uniqueEncryptedStr = Functions.encodeBase64(uniqueEncryptedBytes)

            val dataSend = listOf(mapOf(
                "public_key_E"  to e.toString(),
                "public_key_N"  to n.toString(),
                "unique_number" to uniqueEncryptedStr
            ))

            val response = Api.makeBackendRequestWithResponse("user/verification/check", dataSend, "POST")
            if (response.first == 200)
                return key
        }

        // Otherwise, request one
        key = Functions.fetchUniqueNumber(requireContext())
        val uniqueEncryptedBytes = Functions.encrypt(key.toByteArray(), publicKeyDB.encoded)
        val uniqueEncryptedStr = Functions.encodeBase64(uniqueEncryptedBytes)

        val dataSend = listOf(mapOf(
            "public_key_E"  to e.toString(),
            "public_key_N"  to n.toString(),
            "unique_number" to uniqueEncryptedStr
        ))

        val response = Api.makeBackendRequestWithResponse("user/verification", dataSend, "POST")
        if (response.second == "") {
            Log.e("Database error", "No response when fetching verification key")
            return null
        }

        // Decrypt the response
        val responseBytes = Functions.decodeBase64(response.second)
        val responseBytesDecrypted = Functions.decrypt(requireContext(), responseBytes, mKeyPair.private) ?: return null
        val responseStr = String(responseBytesDecrypted)

        // Store it to data/preferences and return
        requireContext().dataStore.edit {
            it[preferenceKey] = responseStr
        }
        return responseStr
    }
}