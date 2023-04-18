package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.databinding.FragmentActLogBinding
import cryogenetics.logistics.ui.inventory.ActLogViewModel
import cryogenetics.logistics.ui.inventory.JsonAdapter

class ActLogFragment : Fragment() {

    companion object {
        fun newInstance() = ActLogFragment()
    }

    private var _binding : FragmentActLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: ActLogViewModel
    private lateinit var mProductListAdapter: JsonAdapter

    private val mOnProductClickListener =
        AdapterView.OnItemClickListener { parent, view, position, id ->

            /*
            fun onUpdate(position: Int, model: InventoryDataModel) {
                // Add model we want to update to modelToBeUpdated
                modelToBeUpdated.add(model)

                // Set the value of the clicked model in the edit text
                binding.HeaderName?.setText(model.name)
            }

            fun onDelete(model: InventoryDataModel, checkd: Boolean) {
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
        _binding = FragmentActLogBinding.inflate(inflater, container, false)
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
        binding.recyclerViewActLog.adapter = ActLogAdapter(itemList, viewIds)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[ActLogViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchActLogData() :  List<Map<String, Any>>{
        val urlDataString = Api.fetchJsonData("http://10.0.2.2:8080/api/transaction")
        return Api.parseJsonArray(urlDataString)
    }

}