package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiCalls
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.databinding.FragmentMiniActLogBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.host.HostFragment

class MiniActLogFragment (
    private val tvActLogRNrVisible: Boolean = false,
    private val serialNr: String = "null"
        ) : Fragment() {

    private var _binding : FragmentMiniActLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiniActLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize the recyclerView
        binding.recyclerViewActLog.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewActLog.setHasFixedSize(true)

        binding.bCompleteLog.setOnClickListener {
            HostFragment.openAndAddFragment(HostFragment.returnHostFragment(), ActLogFragment(serialNr), "Log[$serialNr]", R.drawable.recent_transactions)
        }

        if (tvActLogRNrVisible) binding.tvActLogRNr.visibility = View.VISIBLE

        // initialize the recyclerView-adapter
        val itemList = mutableListOf<Map<String, Any>>()
        //Fetch json data and add to itemlist

        if (serialNr == "null") {
            for (model in ApiCalls.fetchActLogData())
                itemList.add(if (model.isNotEmpty()) Functions.enforceNumberFormat(model) else model)
        } else {
            val urlDataString = Api.fetchJsonData(ApiUrl.urlTransaction + "?container_sr_number=$serialNr")
            for (model in Api.parseJsonArray(urlDataString))
                itemList.add(if (model.isNotEmpty()) Functions.enforceNumberFormat(model) else model)
        }
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
        itemList.reverse()
        //Create adapter
        binding.recyclerViewActLog.adapter = JsonAdapter(
            itemList, viewIds, R.layout.mini_act_log_recycler_item, tvActLogRNrVisible = tvActLogRNrVisible)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}