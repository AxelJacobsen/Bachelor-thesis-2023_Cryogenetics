package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.databinding.FragmentActLogBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.ui.filters.FilterManager
import cryogenetics.logistics.ui.inventory.ActLogViewModel
import cryogenetics.logistics.ui.inventory.JsonAdapter

class ActLogFragment : Fragment() {

    companion object {
        fun newInstance() = ActLogFragment()
    }

    private var _binding : FragmentActLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ActLogViewModel
    private lateinit var bFilter: Button

    private lateinit var mAdapter: ActLogAdapter
    private lateinit var mActLogFilterFragment: ActLogFilterFragment

    private var filterManager: FilterManager = FilterManager()

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
        bFilter = view.findViewById(R.id.bFilter)
        fetchActLogData()

        // Attach listener to filter button
        bFilter.setOnClickListener {

            // Create filter fragment with an initial filter state
            if (!::mActLogFilterFragment.isInitialized) {
                mActLogFilterFragment = ActLogFilterFragment(
                    {},
                    filterManager
                )

                // Add its on apply function
                mActLogFilterFragment.onApply = {
                    // Close the fragment
                    childFragmentManager.commit {
                        hide(mActLogFilterFragment)
                    }

                    fetchActLogData(filterManager.getUrl(ApiUrl.urlTransaction))
                }

                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(R.id.actLogFragment, mActLogFilterFragment)
                }
            } else {
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    show(mActLogFilterFragment)
                }
            }
        }
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

    /**
     *  Fetches act log data and updates the adapter.
     *
     *  @param forceUrl - url to fetch data from. If left empty, data is fetched from the default url.
     */
    private fun fetchActLogData(forceUrl: String = "") {
        // Fetch and parse data
        val url = if (forceUrl=="") ApiUrl.urlTransaction else forceUrl
        val urlDataString = Api.fetchJsonData(url)
        val parsedData = Api.parseJsonArray(urlDataString)

        // Create a list out of it
        val itemList = mutableListOf<Map<String, Any>>()
        for (model in parsedData)
            itemList.add( if (model.isNotEmpty()) Functions.enforceNumberFormat(model) else model ) 

        // If the adapter doesn't exist, create it
        if (binding.recyclerViewActLog.adapter == null) {
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
            mAdapter = ActLogAdapter(itemList, viewIds)
            binding.recyclerViewActLog.adapter = mAdapter
        // Otherwise, update its data
        } else {
            mAdapter.updateData(itemList)
        }
    }
}