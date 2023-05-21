package cryogenetics.logistics.ui.confirm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.dataStore
import cryogenetics.logistics.databinding.FragmentConfirmBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.tank.OnItemClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class ConfirmFragment(
    private val confirmData: List<Map<String, Any>>,
    private val mOnProductClickListener: OnItemClickListener? = null,
    private val ref: String
) : Fragment() {
    private var userName = ""
    private var userId = 0
    private var _binding: FragmentConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set employee_alias text
        val key1 = stringPreferencesKey("employee_alias")
        val flow1: Flow<String> = requireContext().dataStore.data
            .map {
                it[key1] ?: "No name found"
            }
        runBlocking (Dispatchers.IO) {
            userName = flow1.first()
        }

        val key2 = intPreferencesKey("employee_id")
        val flow2: Flow<Int> = requireContext().dataStore.data
            .map {
                it[key2] ?: 0
            }
        runBlocking(Dispatchers.IO) {
            userId = flow2.first()
        }

        binding.bCancel.setOnClickListener {
            mOnProductClickListener?.onCloseFragment("Conf")
        }

        // initialize the recyclerView
        binding.recyclerConfirm.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerConfirm.setHasFixedSize(true)

        when (ref) {
            "TankFillOverView" -> {
                binding.tvTitle.text = "Confirm Tank Filling"
                binding.tvDescription.text = "Can you confirm that these tanks are full with liquid nitrogen?"
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
                    confirmData as MutableList<Map<String, Any>>, viewIds, R.layout.tank_fill_confirm_recycler)
                binding.tankFillConfirm.visibility = View.VISIBLE
                binding.tankFillPreview.visibility = View.GONE
                binding.clRowFive.visibility = View.VISIBLE
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
        binding.recyclerPreview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPreview.setHasFixedSize(true)

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
        binding.recyclerPreview.adapter = JsonAdapter(previewDatas, viewIds, R.layout.act_log_recycler_item,tvActLogRNrVisible = true)
        binding.tankFillConfirm.visibility = View.GONE
        binding.tankFillPreview.visibility = View.VISIBLE
        binding.clRowFive.visibility = View.GONE
    }

    private fun multipleRefill(refillData: List<Map<String, Any>> = mutableListOf()):
            Triple<MutableList<Map<String, Any>>, MutableList<Map<String, Any>>, MutableList<Map<String, Any>>> {
        val actDatas: MutableList<Map<String, Any>> = mutableListOf()
        val tankDatas: MutableList<Map<String, Any>> = mutableListOf()
        val previewData: MutableList<Map<String, Any>> = mutableListOf()

        for (tank in refillData) {
            actDatas.add(
                mapOf(
                    "container_sr_number" to tank.entries.find { it.key == "container_sr_number" }?.value.toString(),
                    "act" to "Refilled",
                    "comment" to binding.etComment.text.toString(),
                    "employee_id" to userId,
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
                    "primary" to "container_sr_number",
                    "last_filled" to Functions.getDate().toString(),
                )
            )
            previewData.add(
                mapOf(
                    "id" to tank.entries.find { it.key == "id" }?.value.toString(),
                    "container_sr_number" to tank.entries.find { it.key == "container_sr_number" }?.value.toString(),
                    "act" to "Refilled",
                    "address" to tank.entries.find { it.key == "location_name" }?.value.toString(),
                    "comment" to binding.etComment.text.toString(), // Todo: Individual comment implementation
                    "employee_alias" to userName,
                    "client_name" to tank.entries.find { it.key == "client_name" }?.value.toString(),
                    "location_name" to tank.entries.find { it.key == "location_name" }?.value.toString(),
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