package cryogenetics.logistics.ui.inventory

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
import cryogenetics.logistics.databinding.FragmentInventoryBinding
import java.util.*


class InventoryFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryFragment()
    }

    private var _binding : FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: InventoryViewModel
    private lateinit var mProductListAdapter: JsonAdapter

    private var modelToBeUpdated: Stack<InventoryDataModel> = Stack()

    private val mOnProductClickListener = object : AdapterView.OnItemClickListener {
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
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.HeaderPayment?.text = "TESTING123"

        // initialize the recyclerView
        inventoryList = view.findViewById(R.id.InventoryRecycler)
        inventoryList.layoutManager = LinearLayoutManager(requireContext())
        inventoryList.setHasFixedSize(true)

        // initialize the recyclerView-adapter
        val itemList = mutableListOf<Map<String, Any>>()
        //Fetch json data and add to itemlist
        for (model in fetchInventoryData()) {
            itemList.add(model)
        }
        //Create a list of reffrences
        val viewIds = listOf(
                R.id.tvInventoryNr,
                R.id.tvInventoryClient,
                R.id.tvInventoryLocation,
                R.id.tvInventoryInvoice,
                R.id.tvInventoryLastFill,
                R.id.tvInventoryNoti,
                R.id.tvInventoryStatus
                //, R.id.tvInventoryTitle // Cant be found
                )
        //Create adapter
        //val adapter = JsonAdapter(itemList, viewIds)
        //mProductListAdapter = adapter
        inventoryList.adapter = JsonAdapter(itemList, viewIds)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchInventoryData() :  List<Map<String, Any>>{
        val urlDataString = Api.fetchJsonData("http://10.0.2.2:8080/api/transactions")
        return Api.parseJsonArray(urlDataString)
    }

}