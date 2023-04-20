package cryogenetics.logistics.ui.tank.tankMenu

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import cryogenetics.logistics.api.Api.Companion.makeBackendRequest
import cryogenetics.logistics.databinding.FragmentTankManualActBinding
import cryogenetics.logistics.ui.tank.TankData
import java.util.*


class ManualActFragment : Fragment() {

    var cal = Calendar.getInstance()
    private var _binding: FragmentTankManualActBinding? = null
    private val binding get() = _binding!!
    private lateinit var lastFillDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var invoiceDateListener: DatePickerDialog.OnDateSetListener
    private lateinit var mTank: TankData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mTank = arguments?.getParcelable("tankData")!!
        /* todo: delete */

        println("data" + mTank)
        println("datatata")
        println(mTank.address)


        _binding = FragmentTankManualActBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mTank.temp_id != null) {
            binding.etAddress.setText(mTank.address)
            binding.etNote.setText(mTank.comment)
            binding.tvInvoice.text = mTank.invoice
            binding.tvLastFilled.text = mTank.last_filled
            //binding.spinnerStatus.setSelection(mTank.container_status_name)
            //println("dTank.maintenance_needed!!.toInt()" + mTank.maintenance_needed!!.toInt())
            binding.spinnerMaintStatus.setSelection(mTank.maintenance_needed!!.toInt())
            binding.spinnerAffiliatedLab.setSelection(mTank.location_id!!.toInt())
        }

        // create an OnDateSetListener
        lastFillDateListener = datePickListener("lastFillDateListener")
        invoiceDateListener = datePickListener("invoiceDateListener")

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        binding.tvLastFilled.setOnClickListener { datePickDiag(lastFillDateListener) }
        binding.ibLastFilled.setOnClickListener { datePickDiag(lastFillDateListener) }
        binding.tvInvoice.setOnClickListener { datePickDiag(invoiceDateListener) }
        binding.clInvoice.setOnClickListener { datePickDiag(invoiceDateListener) }

        val customList = listOf(
            "null", "Internal", "Sent out", "Returned", "Refilled",
            "Linked", "Unlinked", "Maint. need", "Maint. compl", "Sold", "Discarded")
        val customList1 = listOf("In use", "Quarantine", "Available", "At Client", "Disposed")
        val customList2 = listOf("Hamar", "Trondheim")
        val customList3 = listOf("Maintained", "Needs Maintenance")
        spinnerArrayAdapter(customList, binding.spinnerAct)
        spinnerArrayAdapter(customList1, binding.spinnerStatus)
        spinnerArrayAdapter(customList2, binding.spinnerAffiliatedLab)
        spinnerArrayAdapter(customList3, binding.spinnerMaintStatus)

        binding.bConfirm.setOnClickListener {
            println(getDateTime())
            if (binding.spinnerAct.selectedItem.toString() == "null") {
                Toast.makeText(
                    requireContext(),
                    "Act is not entered, cannot be null!",
                    Toast.LENGTH_LONG
                ).show()
                println("act " + binding.spinnerAct.selectedItem.toString())

            } else {
                val actData = listOf(
                    mapOf(
                        "container_sr_number" to mTank.container_sr_number.toString(),
                        "act" to binding.spinnerAct.selectedItem.toString(),
                        "comment" to binding.etComment.text.toString(),
                        "employee_id" to 103,
                        "client_id" to 1,
                        "location_id" to binding.spinnerAffiliatedLab.selectedItemPosition,
                        // TODO: maybe change to location_id"location_name" to binding.spinnerAffiliatedLab.selectedItem.toString(),
                        "address" to binding.etAddress.text.toString(),
                        "container_status_name" to binding.spinnerStatus.selectedItem.toString(),
                        "date" to getDateTime().toString()
                    )
                )
                val dankData = listOf(
                    mapOf(
                        "container_sr_number" to mTank.container_sr_number.toString(), // Primary
                        "primary" to "container_sr_number", // Must be after Primary-key srNumb
                        "comment" to binding.etNote.text.toString(),
                        "client_id" to 1,  // TODO: add client correctly
                        "location_id" to binding.spinnerAffiliatedLab.selectedItemPosition,
                        //"location_name" to binding.spinnerAffiliatedLab.selectedItem.toString(),
                        // TODO: maybe change to location_id"location_name" to binding.spinnerAffiliatedLab.selectedItem.toString(),
                        "address" to binding.etAddress.text.toString(),
                        "container_status_name" to binding.spinnerStatus.selectedItem.toString(),
                        "maintenance_needed" to binding.spinnerMaintStatus.selectedItemPosition,
                        "last_filled" to binding.tvLastFilled.text.toString(),
                        "invoice" to binding.tvInvoice.text.toString(),
                    )
                )

                val result1 = makeBackendRequest("transaction", actData, "POST")
                val result2 = makeBackendRequest("container", dankData, "PUT")
                println("res1" + result1.toString() + "res2" + result2.toString())
                Toast.makeText(requireContext(), "res1" + result1.toString() + "res2" + result2.toString(), Toast.LENGTH_LONG).show()

            }

        }
    }

    /**
     * Applies a list to a spinner
     */
    private fun spinnerArrayAdapter(list: List<String>, spinner: Spinner) {
        spinner.adapter = object : ArrayAdapter<String>(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            list
        )
        /** TODO: Delete this and drop null? */
        {
            override fun getDropDownView(
                position: Int, convertView: View?, parent: ViewGroup
            ): View {
                val view: TextView =
                    super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray, ensure the rest is always black (necessary).
                if (position == 0) view.setTextColor(Color.RED) else view.setTextColor(Color.BLACK)
                return view
            }
        }
    }

    private fun getDateTime(): String? {
        val myFormat = "yyyy-MM-dd HH:mm:ss"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        val date = Date()
        return dateFormat.format(date)
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
     */
    private fun datePickListener(listenerRef: String): DatePickerDialog.OnDateSetListener {
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
    private fun updateDateInView(listenerRef: String) {
        val myFormat = "dd/MM/yyyy" // mention the format you need, needs to be big M, small d/y.
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        when (listenerRef) {
            "lastFillDateListener" -> {
                binding.tvLastFilled.text = sdf.format(cal.time)
            }
            "invoiceDateListener" -> {
                binding.tvInvoice.text = sdf.format(cal.time)
            }
        }
    }
}