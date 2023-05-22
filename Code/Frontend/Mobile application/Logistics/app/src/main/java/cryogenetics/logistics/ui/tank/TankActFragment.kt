package cryogenetics.logistics.ui.tank

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.Fragment
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.Api.Companion.makeBackendRequest
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.dataStore
import cryogenetics.logistics.databinding.FragmentTankActBinding
import cryogenetics.logistics.functions.Functions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ActFragment(
    private val mOnProductClickListener: OnItemClickListener? = null,
    private val actReference: String = "",
) : Fragment() {

    private var cal = Calendar.getInstance()
    private var _binding: FragmentTankActBinding? = null
    private val binding get() = _binding!!
    private lateinit var lastFillDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var invoiceDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var mTank: TankData
    private var userId = 0
    private var locationIds: MutableList<String> = mutableListOf()
    private var locationNames: MutableList<String> = mutableListOf()
    private var statusNames: MutableList<String> = mutableListOf()
    private var actNames: MutableList<String> = mutableListOf()
    private var clientNames: MutableList<String> = mutableListOf()
    private var clientIds: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        mTank = arguments?.getParcelable("tankData")!!
        _binding = FragmentTankActBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // create an OnDateSetListener
        lastFillDateListener = datePickListener("lastFillDateListener")
        invoiceDateListener = datePickListener("invoiceDateListener")

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        binding.tvLastFilled.setOnClickListener { datePickDiag(lastFillDateListener) }
        binding.ibLastFilled.setOnClickListener { datePickDiag(lastFillDateListener) }
        binding.tvInvoiceDate.setOnClickListener { datePickDiag(invoiceDateListener) }
        binding.ibInvoiceDate.setOnClickListener { datePickDiag(invoiceDateListener) }

        val key2 = intPreferencesKey("employee_id")
        val flow2: Flow<Int> = requireContext().dataStore.data
            .map {
                it[key2] ?: 0
            }
        runBlocking(Dispatchers.IO) {
            userId = flow2.first()
        }

        // Check if mTank was successfully initialized
        if (mTank.id != null) {
            if(fetchSpinnerData(ApiUrl.urlLocation, "location_name", locationNames,
                    "location_id", locationIds))
                spinnerArrayAdapter(locationNames, binding.spinnerAffiliatedLab)

            if(fetchSpinnerData(ApiUrl.urlClient, "client_name", clientNames,
                    "client_id", clientIds))
                spinnerArrayAdapter(clientNames, binding.spinnerClient)

            if(fetchSpinnerData(ApiUrl.urlAct, "act_name", actNames))
                spinnerArrayAdapter(actNames, binding.spinnerAct)

            if(fetchSpinnerData(ApiUrl.urlStatus, "container_status_name", statusNames))
                spinnerArrayAdapter(statusNames, binding.spinnerStatus)

            spinnerArrayAdapter(listOf("Maintained", "Needs Maintenance"), binding.SpinnerAllMaintCompl)
            spinnerArrayAdapter(listOf("Maint need", "Maint compl"), binding.spinnerMaintType)
            spinnerArrayAdapter(listOf("Discarded", "Sold"), binding.spinnerDisposeType)
            binding.etAddress.setText(mTank.address!!.toString())
            binding.etNote.setText(mTank.comment!!.toString())
            binding.tvLastFilled.text = mTank.last_filled!!.toString()
            binding.tvInvoiceDate.text = mTank.invoice!!.toString()
            binding.spinnerMaintType.setSelection(mTank.maintenance_needed!!.toInt())
            binding.spinnerAffiliatedLab.setSelection(getIndex(binding.spinnerAffiliatedLab, mTank.location_name.toString()))

            actHandler(actReference)
        } else {
            Log.d("tag", "Failed to initialize")
            println("Failed to initialize")
        }
    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    @SuppressLint("SetTextI18n")
    private fun actHandler(type: String) = when (type) {
        "Linked" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clAffiliatedLab.visibility = View.INVISIBLE
            binding.clRowFour.visibility = View.GONE
            binding.clRowFourOne.visibility = View.GONE
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.tvManualAct.text = "Link Tank to client"
            binding.tvManualActDescription.text =
                "Linking of a tank to a client is done when the tanks has," +
                    "or should be filled with product. When linking a tank, you will have to choose an existing client," +
                    "clients can only be added by admins. Unlink the tank when it will no longer will be used by the client, which will make it available."

            binding.bConfirm.setOnClickListener {
                mTank.act = "Linked"
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.client_id = clientIds[binding.spinnerClient.selectedItemPosition]
                mTank.container_status_name = "In use"

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Sent out" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clRowThree.visibility = View.GONE
            binding.clLastFilled.visibility = View.INVISIBLE
            binding.clRowFourOne.visibility = View.GONE
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.tvManualAct.text = "Send to client"
            binding.tvManualActDescription.text =
                "When a tank is sent out to a client, it leaves cryogenetics facilities and product may be extracted." +
                    "Please enter a suitable address, in case the tank does not return it will be used to track down the tanks location."

            binding.bConfirm.setOnClickListener {
                mTank.act = "Sent out"
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.address = binding.etAddress.text.toString()
                mTank.container_status_name = "At client"

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Returned" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clRowThree.visibility = View.GONE
            binding.clRowFour.visibility = View.GONE
            binding.clRowFourOne.visibility = View.GONE
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.tvManualAct.text = "Return from client"
            binding.tvManualActDescription.text =
                "When a tank is returned from a client it needs to be in quarantine for a set time period." +
                    "When quarantine is over status will change to “Available” or “In use” depending on if it is still linked."

            binding.bConfirm.setOnClickListener {
                mTank.act = "Returned"
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.address = ""
                mTank.container_status_name = "Quarantine"

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Internal" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clClient.visibility = View.INVISIBLE
            binding.clRowFour.visibility  = View.GONE
            binding.clRowFourOne.visibility = View.GONE
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.tvManualAct.text = "Internal Transfer"
            binding.tvManualActDescription.text =
                "Internal transfers of tanks is only possible between facilities within the same country. Send a tank to another Cryogenetics facility."

            binding.bConfirm.setOnClickListener {
                mTank.act = "Internal"
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.location_id = locationIds[binding.spinnerAffiliatedLab.selectedItemPosition]
                mTank.address = ""
                mTank.container_status_name = "Available"

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Maintenance" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clRowThree.visibility = View.GONE
            binding.clRowFour.visibility = View.GONE
            binding.clRowFourOne.visibility = View.VISIBLE
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.VISIBLE
            binding.bConfirm.text = "Register Maintenance"
            binding.tvManualAct.text = "Manage Maintenance"
            binding.tvManualActDescription.text =
                "Maintenance includes all actions which preserve the tank’s condition. " +
                    "When a user reports that maintenance is needed, it will have a “Maintenance need” notification until all maintenance complete is reported."

            binding.bConfirm.setOnClickListener {
                mTank.act = binding.spinnerMaintType.selectedItem.toString()
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.maintenance_needed = binding.SpinnerAllMaintCompl.selectedItem.toString()

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Refilled" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clRowThree.visibility = View.GONE
            binding.clRowFour.visibility = View.GONE
            binding.clRowFourOne.visibility = View.GONE
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.tvManualAct.text = "Refill Cryogenic Fluid"
            binding.tvManualActDescription.text = "Refill cryogenic fluid before the product is ruined, you can refill multiple tanks at once using Tank filling." // +
            //"Older tanks may dissipate cryogenic fluid faster than others, change cryo dissipation  color code if this is the case."
            // TODO IMPLEMENT 2 EXTRA Buttons? (They are a bit unnecessary.)

            binding.bConfirm.setOnClickListener {
                mTank.act = "Refilled"
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.last_filled = Functions.getDate().toString()

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Dispose" -> {
            binding.clRowOne.visibility = View.GONE
            binding.clRowTwo.visibility = View.GONE
            binding.clRowThree.visibility = View.GONE
            binding.clRowFour.visibility = View.GONE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.clAllMaintCompl.visibility = View.INVISIBLE
            binding.clDisposeType.visibility = View.VISIBLE
            binding.tvManualAct.text = "Manage Maintenance"
            binding.tvManualActDescription.text = "Dispose a tank when it is no longer can be used to store product, or has been sold to another party. This can be reversed for eventual edge cases."

            binding.bConfirm.setOnClickListener {
                mTank.act = binding.spinnerDisposeType.selectedItem.toString()
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.container_status_name = "Disposed"

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()
            }
        }
        "Manual" -> {
            binding.clDisposeType.visibility = View.INVISIBLE
            binding.clMaintType.visibility = View.INVISIBLE
            binding.tvManualAct.text = "Manual Act"
            binding.tvManualActDescription.text = "Manual Act should ONLY be used for cases where another act cannot be used," +
                    "when used please be cautious to avoid possible errors. All Manual Acts will be marked with [M]."
            binding.bConfirm.setOnClickListener {
                mTank.act = binding.spinnerAct.selectedItem.toString()
                mTank.comment_for_act = binding.etComment.text.toString() // COMMENT FROM USER
                mTank.comment = binding.etNote.text.toString() // NOTE
                mTank.client_id = clientIds[binding.spinnerClient.selectedItemPosition]
                mTank.location_id = locationIds[binding.spinnerAffiliatedLab.selectedItemPosition]
                mTank.address = binding.etAddress.text.toString()
                mTank.container_status_name = binding.spinnerStatus.selectedItem.toString()
                mTank.maintenance_needed = binding.spinnerMaintType.selectedItemPosition.toString()
                mTank.last_filled = postRightDate(binding.tvLastFilled.text.toString()).toString()
                mTank.invoice = postRightDate(binding.tvInvoiceDate.text.toString()).toString()

                if (postAndPushData())
                    Toast.makeText(requireContext(), "Successful upload", Toast.LENGTH_SHORT).show()
                 else
                    Toast.makeText(requireContext(), "Upload failed!", Toast.LENGTH_LONG).show()

            }
        }
        else -> {
            Log.d("tag", "Failed to initialize")
            Toast.makeText(requireContext(), "Failed to initialize", Toast.LENGTH_LONG).show()
        }
    }

    private fun postAndPushData(): Boolean {
        val result1 = makeBackendRequest("transaction", getActData(), "POST")
        val result2 = makeBackendRequest("container", getTankData(), "PUT")

        mOnProductClickListener?.updateTankData(getComplTankData())
        println("res1" + result1.toString() + "res2" + result2.toString())

        return result1 == 200 && result2 == 200
    }

    private fun getActData(): List<Map<String, Any>> {
        return listOf(mapOf(
            "container_sr_number" to mTank.container_sr_number.toString(),
            "act" to mTank.act.toString(),
            "comment" to mTank.comment_for_act.toString(),
            "employee_id" to userId,
            "client_id" to mTank.client_id.toString(),
            "location_id" to mTank.location_id.toString(),
            "address" to mTank.address.toString(),
            "container_status_name" to mTank.container_status_name.toString(),
            "date" to Functions.getDateTime().toString()
        ))
    }

    private fun getTankData(): List<Map<String, Any>> {
        return listOf(mapOf(
            "container_sr_number" to mTank.container_sr_number.toString(), // Primary
            "primary" to "container_sr_number", // Must be after Primary-key srNumb
            "comment" to mTank.comment.toString(),
            "client_id" to mTank.client_id.toString(),
            "location_id" to mTank.location_id.toString(),
            "address" to mTank.address.toString(),
            "container_status_name" to mTank.container_status_name.toString(),
            "maintenance_needed" to mTank.maintenance_needed.toString(),
            "last_filled" to mTank.last_filled.toString(),
            "invoice" to mTank.invoice.toString(),
        ))
    }

    private fun getComplTankData(): List<Map<String, Any>> {
        return listOf(mapOf(
            "container_sr_number" to mTank.container_sr_number.toString(), // Primary
            "id" to mTank.id.toString(),
            "comment" to binding.etNote.text.toString(),
            "client_id" to clientIds[binding.spinnerClient.selectedItemPosition],
            "client_name" to binding.spinnerClient.selectedItem.toString(),
            "location_id" to locationIds[binding.spinnerAffiliatedLab.selectedItemPosition],
            "location_name" to binding.spinnerAffiliatedLab.selectedItem.toString(),
            "address" to binding.etAddress.text.toString(),
            "container_status_name" to binding.spinnerStatus.selectedItem.toString(),
            "maintenance_needed" to (binding.spinnerMaintType.selectedItemPosition),
            "last_filled" to postRightDate(binding.tvLastFilled.text.toString()).toString(),
            "invoice" to postRightDate(binding.tvInvoiceDate.text.toString()).toString(),
        ))
    }

    /**
     * Posts right date in the format sql uses.
     * @param inputString - Has to be a date or null.
     */
    private fun postRightDate(inputString: String): String? {
        if (inputString == "0000-00-00" || inputString == "null") // Ensures that formatter doesn't fail if date = 00...
            return "null"

        val formatterDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatterAndroid = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date = LocalDate.parse(inputString, formatterAndroid)
        return date.format(formatterDb)
    }

    /**
     * Fetches spinner and applies it to the spinner.
     * @param url - The url of where to get the data for spinna.
     * @param key_name - A reference to this the data we are get'in
     * @param listNames - The names we will be looking for in the data.
     * @param key_id - The id we will be looking for in the data.
     * @param listIds - Ids of where the data should be applied.
     */
    private fun fetchSpinnerData(
        url: String,
        key_name: String,
        listNames: MutableList<String>,
        key_id: String = "0",
        listIds: MutableList<String> = mutableListOf(),
    ): Boolean {
        val urlDataString = Api.fetchJsonData(url)
        val fetchedData = Api.parseJsonArray(urlDataString)
        if (fetchedData.isNotEmpty()) {
            for (model in fetchedData) {
                listNames.add(model.entries.find { it.key == key_name }?.value.toString())
                if (key_id != "0")
                    listIds.add(model.entries.find { it.key == key_id }?.value.toString())
            }
        } else {
            Toast.makeText(requireContext(), "No $key_name data added!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /**
     * Applies a list to a spinner
     */
    private fun spinnerArrayAdapter(list: List<String>, spinner: Spinner) {
        spinner.adapter = object : ArrayAdapter<String>(requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list
        ) {
            override fun getDropDownView(
                position: Int, convertView: View?, parent: ViewGroup,
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                //set the color of elements to black
                view.setTextColor(Color.BLACK)
                return view
            }
        }
    }

    /**
     * Common datePickerDialog body.
     */
    private fun datePickDiag(dateChangeListener: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
            requireContext(),
            dateChangeListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Common onDateSetListener body.
     * @param listenerRef - The refrence of the listener
     */
    private fun datePickListener(listenerRef : String): DatePickerDialog.OnDateSetListener {
        val listener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView(listenerRef)
            }
        return listener
    }

    /**
     * Updates date in textView
     * @param listenerRef - The refrence of the listener
     */
    private fun updateDateInView(listenerRef : String) {
        val myFormat = "dd-MM-yyyy" // mention the format you need, needs to be big M, small d/y.
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        when(listenerRef) {
            "lastFillDateListener" -> {
                binding.tvLastFilled.text = sdf.format(cal.time)
            }
            "invoiceDateListener" -> {
                binding.tvInvoiceDate.text = sdf.format(cal.time)
            }
        }
    }
}