package cryogenetics.logistics.ui.tank.tankMenu

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import cryogenetics.logistics.api.Api.Companion.makeBackendRequest
import cryogenetics.logistics.databinding.FragmentTankManualActBinding
import java.util.*

class   ManualActFragment : Fragment() {

    var cal = Calendar.getInstance()
    private var _binding : FragmentTankManualActBinding? = null
    private val binding get() = _binding!!
    private lateinit var lastFillDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var invoiceDateListener: DatePickerDialog.OnDateSetListener
   // private lateinit var viewModel: DashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTankManualActBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get the references from layout file

        // create an OnDateSetListener
        lastFillDateListener = datePickListener("lastFillDateListener")
        invoiceDateListener = datePickListener("invoiceDateListener")

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        binding.tvLastFilled.setOnClickListener { datePickDiag(lastFillDateListener) }
        binding.ibLastFilled.setOnClickListener { datePickDiag(lastFillDateListener) }
        binding.tvInvoice.setOnClickListener { datePickDiag(invoiceDateListener) }
        binding.clInvoice.setOnClickListener { datePickDiag(invoiceDateListener) }

        val customList = listOf("null", "Internal", "Sent out", "Returned", "Linked", "Unlinked", "Maint need", "Maint compl",  "Sold", "Discarded")
        val customList1 = listOf("null", "In use", "Quarantine", "Available", "At Client", "Disposed")
        val customList2 = listOf("null", "Hamar", "Trondheim")
        val customList3 = listOf("null", "Needs Maintenance", "Maintained")
        spinnerArrayAdapter(customList, binding.spinnerAct)
        spinnerArrayAdapter(customList1, binding.spinnerStatus)
        spinnerArrayAdapter(customList2, binding.spinnerAffiliatedLab)
        spinnerArrayAdapter(customList3, binding.spinnerMaintStatus)

        binding.bConfirm.setOnClickListener {
            val dataList = listOf(
                mapOf("address" to "Wow this is one ugly container", "model" to "large200", "primary" to "model"),
                mapOf("address" to "TestAdresse", "model" to "verySmall60", "primary" to "model")
            )
            makeBackendRequest("user/container", dataList, "POST")
        }
        //POST EXAMPLE, make sure all fields that are non-nullable are provided
        /*
        val dataList = listOf(

            mapOf(  "serial_number" to 123321, "country_iso3" to "KYS",
                    "model" to "large200", "status" to "Quarantine")
        )

        //PUT EXAMPLE, primary must be identical to a provided field
        val dataList = listOf(
            mapOf("address" to "Wow this is one ugly container", "model" to "large200", "primary" to "model"),
            mapOf("address" to "TestAdresse", "model" to "verySmall60", "primary" to "model")
        )
        */
        //NEED TO UPDATE URL TO MATCH LOCAL VERISON OF BACKEND
        //makeBackendRequest("user/container", dataList, "POST")
    }

    /**
     * Applies a list to a spinner
     */
    private fun spinnerArrayAdapter (list: List<String>, spinner :Spinner) {
        spinner.adapter = object : ArrayAdapter<String>(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            list
        ) /** TODO: Delete this and drop null? */{
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray, ensure the rest is always black (necessary).
                if(position == 0) view.setTextColor(Color.RED) else view.setTextColor(Color.BLACK)
                return view
            }
        }
    }

    /**
     * Common datePickerDialog body.
     */
    private fun datePickDiag(dateChangeListener : DatePickerDialog.OnDateSetListener) {
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
     */
    private fun datePickListener(listenerRef : String) :DatePickerDialog.OnDateSetListener {
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
     */
    private fun updateDateInView(listenerRef : String) {
        val myFormat = "dd/MM/yyyy" // mention the format you need, needs to be big M, small d/y.
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        when(listenerRef) {
            "lastFillDateListener" -> {
                binding.tvLastFilled.text = sdf.format(cal.time)
            }
            "invoiceDateListener" -> {
                binding.tvInvoice.text = sdf.format(cal.time)
            }
        }
    }
}