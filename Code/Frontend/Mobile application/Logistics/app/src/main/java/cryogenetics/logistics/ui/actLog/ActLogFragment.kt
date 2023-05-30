package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.databinding.FragmentActLogBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.filters.FilterManager
import cryogenetics.logistics.ui.inventory.InventoryFilterFragment

class ActLogFragment(
    private val serialNr: String = "null"
) : Fragment() {
    private lateinit var mAdapter: JsonAdapter
    private lateinit var mActLogFilterFragment: ActLogFilterFragment

    private var _binding : FragmentActLogBinding? = null
    private val binding get() = _binding!!
    private var filterManager: FilterManager = FilterManager()
    private var actFragInitialized = false
    private var actData: String = ""

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
        fetchActLogData()

        // Attach listener to filter button
        binding.bFilter.setOnClickListener {
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
                    add(R.id.actLogFragment, mActLogFilterFragment, "actF")
                }
                actFragInitialized = true
            } else {
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    show(mActLogFilterFragment)
                }
            }
            // Set actData back to empty for onPause
            actData = ""
        }
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
        var urlDataString = Api.fetchJsonData(url)
        val parsedData = Api.parseJsonArray(urlDataString)

        // Create a list out of it
        val itemList = mutableListOf<Map<String, Any>>()

        if (serialNr == "null") {
            for (model in parsedData)
                itemList.add( if (model.isNotEmpty()) Functions.enforceNumberFormat(model) else model )
        } else {
            urlDataString = Api.fetchJsonData(ApiUrl.urlTransaction + "?container_sr_number=$serialNr")
            for (model in Api.parseJsonArray(urlDataString))
                itemList.add(if (model.isNotEmpty()) Functions.enforceNumberFormat(model) else model)
        }

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
            itemList.reverse()
            mAdapter = JsonAdapter(itemList, viewIds, R.layout.act_log_recycler_item)
            binding.recyclerViewActLog.adapter = mAdapter
        // Otherwise, update its data
        } else {
            mAdapter.updateData(itemList)
        }
    }

    /**
     * Handles a pause event, by storing data that would otherwise be lost.
     */
    override fun onPause() {
        super.onPause()
        if (actFragInitialized) { // Avoid risk of RuntimeException
            // Find fragment by using the tag
            mActLogFilterFragment = (childFragmentManager.findFragmentByTag("actF")
                ?: throw RuntimeException("Could not find Tag")) as ActLogFilterFragment
            childFragmentManager.commit {
                remove(mActLogFilterFragment)
            } // Remove Fragment to save resources
            childFragmentManager.popBackStack()

            if (actData == "")// If the user has changed the data, get the data from filterManager.
                actData = filterManager.getUrl(ApiUrl.urlTransaction)
        }
    }

    /**
     * Handles restoring values back to the state before user left the tab.
     */
    override fun onResume() {
        super.onResume()
        if (actFragInitialized) {
            // If the actFrag has been initialized, we want to restore its state.
            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.actLogFragment, mActLogFilterFragment, "actF")
                // We also hide/close the fragment, to get the behaviour we want.
                hide(mActLogFilterFragment)
            }

            if (actData == "") // It should always be empty unless there is a tab open in users view.
                fetchActLogData(filterManager.getUrl(ApiUrl.urlTransaction)) // Normal fetch data with filter.
             else
                fetchActLogData(actData) // Restore the last state of the table.

        }
    }

}