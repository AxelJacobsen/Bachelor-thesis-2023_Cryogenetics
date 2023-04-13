package cryogenetics.logistics.ui.inventory.mini

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.databinding.FragmentMiniActLogBinding
import cryogenetics.logistics.databinding.FragmentMiniInventoryBinding
import cryogenetics.logistics.ui.actLog.mini.MiniInventoryAdapter
import cryogenetics.logistics.ui.actLog.mini.MiniInventoryViewModel
import cryogenetics.logistics.ui.inventory.*

class MiniInventoryFragment : Fragment() {

    companion object {
        fun newInstance() = MiniInventoryFragment()
    }

    private var _binding : FragmentMiniInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: MiniInventoryViewModel
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
        _binding = FragmentMiniInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize the recyclerView
        binding.InventoryRecycler
        binding.InventoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.InventoryRecycler.setHasFixedSize(true)

        // initialize the recyclerView-adapter
        val itemList = mutableListOf<Map<String, Any>>()
        //Fetch json data and add to itemlist

        for (model in fetchActLogData()) {
            itemList.add(model)
        }

        //Create a list of references

        val viewIds = listOf(
            R.id.tvInventoryNr,
            R.id.tvInventoryClient,
            R.id.tvInventoryLocation,
            R.id.tvInventoryInvoice,
            R.id.tvInventoryLastFill,
            R.id.tvInventoryNoti,
            R.id.tvInventoryStatus,
            R.id.tvInventorySerialNr
        )
        //Create adapter
        binding.InventoryRecycler.adapter = MiniInventoryAdapter(itemList, viewIds)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[MiniInventoryViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchActLogData() :  List<Map<String, Any>>{
        val urlDataString = Api.fetchJsonData("http://10.0.2.2:8080/api/container")
        return Api.parseJsonArray(urlDataString)
    }

}