package cryogenetics.logistics.ui.login

import android.os.Bundle
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
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.ui.host.HostFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Unconfined


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch components
        bLogin = view.findViewById(R.id.bLogin)
        etLogin = view.findViewById(R.id.etLogin)
        pbLogin = view.findViewById(R.id.pbLogin)

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
}