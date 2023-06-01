package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.api.ApiCalls
import cryogenetics.logistics.api.ApiUrl
import cryogenetics.logistics.databinding.FragmentActLogBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.functions.Functions.Companion.sortChange
import cryogenetics.logistics.functions.JsonAdapter
import cryogenetics.logistics.ui.confirm.DetailsFragment
import cryogenetics.logistics.ui.filters.FilterManager
import cryogenetics.logistics.ui.tank.OnItemClickListener

class ActLogFragment(
    private val serialNr: String = "null"
) : Fragment() {
    private lateinit var mAdapter: JsonAdapter
    private lateinit var mActLogFilterFragment: ActLogFilterFragment
    private lateinit var mListener: OnItemClickListener


    private val sortIVs = listOf(
        R.id.ivActLogRNr,
        R.id.ivActLogRTime,
        R.id.ivActLogRClient,
        R.id.ivActLogRLocation,
        R.id.ivActLogRAct,
        R.id.ivActLogRComment,
        R.id.ivActLogRSign,
        R.id.ivActLogRStatus
    )

    private val tvIds = listOf(
        R.id.tvActLogRNr,
        R.id.tvActLogRTime,
        R.id.tvActLogRClient,
        R.id.tvActLogRLocation,
        R.id.tvActLogRAct,
        R.id.tvActLogRComment,
        R.id.tvActLogRSign,
        R.id.tvActLogRStatus
    )

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

        mListener = mOnFoundProductListener

        binding.tvActLogRNr.setOnClickListener {
            sortChange(binding.tvActLogRNr, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRTime.setOnClickListener {
            sortChange(binding.tvActLogRTime, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRClient.setOnClickListener {
            sortChange(binding.tvActLogRClient, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRLocation.setOnClickListener {
            sortChange(binding.tvActLogRLocation, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRAct.setOnClickListener {
            sortChange(binding.tvActLogRAct, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRComment.setOnClickListener {
            sortChange(binding.tvActLogRComment, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRSign.setOnClickListener {
            sortChange(binding.tvActLogRSign, mAdapter, requireContext(), view, sortIVs)
        }
        binding.tvActLogRStatus.setOnClickListener {
            sortChange(binding.tvActLogRStatus, mAdapter, requireContext(), view, sortIVs)
        }
        binding.bSearch.setOnClickListener {
            val searchRes = Functions.searchContainer(
                requireContext(),
                ApiCalls.fetchActLogData(),
                binding.edSearchValue.text.toString()
            )
            mAdapter.updateData(searchRes)
        }

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

    private val mOnFoundProductListener = object : OnItemClickListener {
        override fun onClick(model: Map<String, Any>) {
            childFragmentManager.beginTransaction()
                .replace(R.id.flActLogDetails, DetailsFragment(listOf( model ),
                    "Dtai", mListener), "Dtai")
                .commit()
            println("onCommentClick bDetails " + model)
        }

        override fun onCloseFragment(tag: String) {
            val swipe = childFragmentManager.findFragmentByTag(tag)
                ?: throw RuntimeException("Could not find Tag: $tag")

            childFragmentManager.beginTransaction()
                .remove(swipe)
                .commit()
            childFragmentManager.popBackStack()
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
            itemList.reverse()
            mAdapter = JsonAdapter(itemList, tvIds, R.layout.act_log_recycler_item, listener = mListener)
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