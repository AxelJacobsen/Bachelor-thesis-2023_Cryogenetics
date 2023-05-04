package cryogenetics.logistics.ui.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.databinding.FragmentConfirmBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.ui.actLog.ActLogAdapter
import cryogenetics.logistics.ui.inventory.JsonAdapter
import cryogenetics.logistics.ui.tank.OnItemClickListener

class ConfirmFragment(
    private val confirmData: List<Map<String, Any>>,
    private val mOnProductClickListener: OnItemClickListener? = null,
    private val ref: String
) : Fragment() {

    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bCancel.setOnClickListener {
            mOnProductClickListener?.onCancelConfirm()
        }

        // initialize the recyclerView
        binding.recyclerConfirm.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerConfirm.setHasFixedSize(true)

        when (ref) {
            "TankFillOverView" -> {
                binding.bConfirm.setOnClickListener {
                    val (actDatas, tankDatas, previewDatas) = multipleRefill(confirmData)
                    displayAndPost(actDatas, tankDatas, previewDatas)
                }

                //Create a list of references
                val viewIds = listOf(
                    R.id.tvInventoryNr,
                    R.id.tvInventoryLastFill
                )
                //Create adapter
                binding.recyclerConfirm.adapter = JsonAdapter(
                    confirmData as MutableList<Map<String, Any>>, viewIds)
            }
        }

    }

    private fun displayAndPost(
        actDatas: MutableList<Map<String, Any>>,
        tankDatas: MutableList<Map<String, Any>>,
        previewDatas: MutableList<Map<String, Any>>
    ) {
        binding.bConfirm.setOnClickListener {
            val result1 = Api.makeBackendRequest("transaction", actDatas, "POST")
            val result2 = Api.makeBackendRequest("container", tankDatas, "PUT")

            println("res1" + result1.toString() + "res2" + result2.toString())
            Toast.makeText(
                requireContext(),
                "res1" + result1.toString() + "res2" + result2.toString(),
                Toast.LENGTH_LONG
            ).show()
        }

        // initialize the recyclerView
        binding.recyclerConfirm.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerConfirm.setHasFixedSize(true)

        //Create a list of references
        val viewIds = listOf(
            R.id.tvActLogRNr,
            R.id.tvActLogRTime,
            R.id.tvActLogRClient,
            R.id.tvActLogRLocation,
            R.id.tvActLogRAct,
            R.id.tvActLogRComment,
            R.id.tvActLogRSign,
            R.id.tvActLogRStatus
        )

        println("actDatas " + actDatas)
        //Create adapter
        binding.recyclerConfirm.adapter = ActLogAdapter(previewDatas, viewIds)
    }

    private fun multipleRefill(refillData: List<Map<String, Any>> = mutableListOf()):
            Triple<MutableList<Map<String, Any>>, MutableList<Map<String, Any>>, MutableList<Map<String, Any>>> {
        val actDatas: MutableList<Map<String, Any>> = mutableListOf()
        val tankDatas: MutableList<Map<String, Any>> = mutableListOf()
        val previewData: MutableList<Map<String, Any>> = mutableListOf()

        binding.tankFillConfirm.visibility = View.INVISIBLE

        for (tank in refillData) {
            actDatas.add(
                mapOf(
                    "container_sr_number" to tank.entries.find { it.key == "container_sr_number" }?.value.toString(),
                    "act" to "Refilled",
                    "comment" to "", // Todo: comment implementation
                    "employee_id" to 103, // TODO: Fix after log in is added
                    "client_id" to tank.entries.find { it.key == "client_id" }?.value.toString(),
                    "location_id" to tank.entries.find { it.key == "location_id" }?.value.toString(),
                    "address" to tank.entries.find { it.key == "address" }?.value.toString(),
                    "container_status_name" to tank.entries.find { it.key == "container_status_name" }?.value.toString(),
                    "date" to Functions.getDateTime().toString(),
                )
            )
            tankDatas.add(
                mapOf(
                    "container_sr_number" to tank.entries.find {
                        it.key == "container_sr_number"
                    }?.value.toString(), // Primary
                    "primary" to "container_sr_number", // Must be after Primary-key srNumb
                    "last_filled" to Functions.getDate().toString(),
                )
            )
            previewData.add(
                mapOf(
                    "id" to tank.entries.find { it.key == "id" }?.value.toString(),
                    "container_sr_number" to tank.entries.find { it.key == "container_sr_number" }?.value.toString(),
                    "act" to "Refilled",
                    "address" to tank.entries.find { it.key == "location_name" }?.value.toString(),
                    "comment" to "Todo: comment implementation", // Todo: comment implementation
                    "employee_id" to 103, // TODO: Fix after log in is added
                    // employee_id does not work with preview, need to get Alias
                    "client_id" to tank.entries.find { it.key == "client_id" }?.value.toString(),
                    "location_id" to tank.entries.find { it.key == "location_id" }?.value.toString(),
                    "address" to tank.entries.find { it.key == "address" }?.value.toString(),
                    "container_status_name" to tank.entries.find { it.key == "container_status_name" }?.value.toString(),
                    "date" to Functions.getDateTime().toString(),
                    "last_filled" to Functions.getDate().toString(),
                )
            )

        }

        return Triple(actDatas, tankDatas, previewData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}