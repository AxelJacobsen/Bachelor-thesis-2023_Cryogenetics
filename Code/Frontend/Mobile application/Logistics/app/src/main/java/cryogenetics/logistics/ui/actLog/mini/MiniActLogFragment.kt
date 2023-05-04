package cryogenetics.logistics.ui.actLog.mini

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.databinding.FragmentMiniActLogBinding

class MiniActLogFragment : Fragment() {

    companion object {
        fun newInstance() = MiniActLogFragment()
    }

    private var _binding : FragmentMiniActLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MiniActLogViewModel

    private val mOnProductClickListener =
        AdapterView.OnItemClickListener { parent, view, position, id ->

            /*
            fun onUpdate(position: Int, model: ActLogDataModel) {
                // Add model we want to update to modelToBeUpdated
                modelToBeUpdated.add(model)

                // Set the value of the clicked model in the edit text
                binding.HeaderName?.setText(model.name)
            }

            fun onDelete(model: ActLogDataModel, checkd: Boolean) {
                // We change the value of isChecked to prepare removal.
                model.isChecked = checkd
            }
            */
            // TODO("Not yet implemented")
        }

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

        // initialize the recyclerView-adapter
        val itemList = mutableListOf<Map<String, Any>>()
        //Fetch json data and add to itemlist

        for (model in fetchActLogData()) {
            itemList.add(model)
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
        //Create adapter
        binding.recyclerViewActLog.adapter = MiniActLogAdapter(itemList, viewIds)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[MiniActLogViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchActLogData() :  List<Map<String, Any>>{
        val urlDataString = Api.fetchJsonData(ApiUrl.urlTransaction)
        return Api.parseJsonArray(urlDataString)
    }

}