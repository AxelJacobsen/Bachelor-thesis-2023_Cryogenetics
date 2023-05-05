package cryogenetics.logistics.ui.inventory

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiCalls
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.databinding.FragmentInventoryBinding
import cryogenetics.logistics.ui.filters.FilterManager
import cryogenetics.logistics.functions.Functions.Companion.enforceNumberFormat

class InventoryFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryFragment()
    }

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: InventoryViewModel
    private lateinit var mProductListAdapter: JsonAdapter

    private lateinit var bFilter: Button

    private var filterManager: FilterManager = FilterManager()
    private lateinit var mInventoryFilterFragment: InventoryFilterFragment
    private lateinit var mAdapter: InventoryAdapter

    //private var modelToBeUpdated: Stack<InventoryDataModel> = Stack()

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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize the recyclerView
        binding.InventoryRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.InventoryRecycler.setHasFixedSize(true)
        bFilter = view.findViewById(R.id.bFilter)
        fetchInventoryData()

        // Attach listener to filter button
        bFilter.setOnClickListener {
            // Create filter fragment with an initial filter state
            if (!::mInventoryFilterFragment.isInitialized) {
                mInventoryFilterFragment = InventoryFilterFragment(
                    {},
                    filterManager
                )

                // Add its on apply function
                mInventoryFilterFragment.onApply = {
                    // Close the fragment
                    childFragmentManager.commit {
                        hide(mInventoryFilterFragment)
                    }

                    fetchInventoryData(filterManager.getUrl(ApiUrl.urlContainer))
                }

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.inventoryFragment, mInventoryFilterFragment)
                }
            } else {
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    show(mInventoryFilterFragment)
                }
            }
        }
    }

    /**
     *  Fetches act log data and updates the adapter.
     *
     *  @param forceUrl - url to fetch data from. If left empty, data is fetched from the default url.
     */
    private fun fetchInventoryData(forceUrl: String = "") {
        // Fetch and parse data
        val url = if (forceUrl=="") ApiUrl.urlContainer else forceUrl
        val urlDataString = Api.fetchJsonData(url)
        val parsedData = Api.parseJsonArray(urlDataString)

        // Create a list out of it
        val itemList = mutableListOf<Map<String, Any>>()
        for (model in parsedData)
            itemList.add( if (model.isNotEmpty()) enforceNumberFormat(model) else model )

        // If the adapter doesn't exist, create it
        if (binding.InventoryRecycler.adapter == null) {
            val viewIds = listOf(
                R.id.tvInventoryNr,
                R.id.tvInventoryClient,
                R.id.tvInventoryLocation,
                R.id.tvInventoryInvoice,
                R.id.tvInventoryLastFill,
                R.id.tvInventorySerialNr,
                R.id.tvInventoryNoti,
                R.id.tvInventoryStatus
            )
            mAdapter = InventoryAdapter(itemList, viewIds)
            binding.InventoryRecycler.adapter = mAdapter
            // Otherwise, update its data
        } else {
            mAdapter.updateData(itemList)
        }
    }
}