package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.api.Api
import cryogenetics.logistics.databinding.FragmentActLogBinding
import cryogenetics.logistics.databinding.FragmentHostBinding
import cryogenetics.logistics.ui.filters.FilterAdapter
import cryogenetics.logistics.ui.filters.FilterFragment
import cryogenetics.logistics.ui.inventory.ActLogViewModel
import cryogenetics.logistics.ui.inventory.JsonAdapter
import cryogenetics.logistics.ui.taskmanager.TaskManagerAdapter


class ActLogFragment : Fragment() {

    companion object {
        fun newInstance() = ActLogFragment()
    }

    private var _binding : FragmentActLogBinding? = null
    private val binding get() = _binding!!

    private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: ActLogViewModel
    private lateinit var mProductListAdapter: JsonAdapter
    private lateinit var bFilter: Button

    private lateinit var filterState: MutableMap<String, Map<String, String>>

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
        bFilter = view.findViewById(R.id.bFilter)

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

        // Attach listener to filter button
        bFilter.setOnClickListener {

            // If filter state hasn't been initialized yet, initialize it.
            if (!::filterState.isInitialized) {
                addTableToFilters("http://10.0.2.2:8080/api/container_status", "status", listOf("container_status_name"))
                addTableToFilters("http://10.0.2.2:8080/api/act", "act", listOf("act_name"))
                addTableToFilters("http://10.0.2.2:8080/api/location", "locations", listOf("location_name"))
                addTableToFilters("http://10.0.2.2:8080/api/container_model", "size", listOf("liter_capacity"))
            }

            val fragment = FilterFragment (
                {filterState = it.toMutableMap()},
                filterState
            )

            childFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.actLogFragment, fragment)
            }
        }
    }

    /**
     *  Adds a database table to the list of filters.
     *
     *  @param url - Url to get the table.
     *  @param shorthand - The shorthand or "name" of the table, frontend only.
     *  @param acceptedKeys - Which keys or "columns" to fetch, if none are given, everything is fetched.
     *  @param default - The default value of the filters, i.e. "true" if all checkboxes should start checked.
     */
    private fun addTableToFilters(url: String, shorthand: String, acceptedKeys: List<String> = emptyList(), default: String = "false") {
        // Fetch fields
        val fields: List<String> = Api.parseJsonArray(Api.fetchJsonData(url))
            .fold(emptyList()) { acc, e ->
                acc + e.filter { acceptedKeys.isEmpty() || acceptedKeys.contains(it.key) }.values.map { it.toString() }
            }

        // Verify that filterState is initialized
        if (!::filterState.isInitialized)
            filterState = mutableMapOf()

        // Add fields to filterState
        filterState[shorthand] = fields.associateWith { default }
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
     *  Gets the columns of a given table from the database.
     *
     *  @param table - The table.
     *
     *  @return All columns associated with the given table in the format (name, type, keytype).
     */
    private fun getColumns(table: String) : List<Triple<String,String,String>> {
        var jsonRaw: String
        try {
            jsonRaw = Api.fetchJsonData("http://10.0.2.2:8080/api/$table/columns")
        } catch (e: Exception) {
            return emptyList()
        }
        val jsonParsed = Api.parseJsonArray(jsonRaw)
        return jsonParsed.map { Triple(
            it["COLUMN_NAME"].toString(),
            it["COLUMN_TYPE"].toString(),
            it["COLUMN_KEY"].toString()
        ) }
    }

    private fun fetchActLogData() :  List<Map<String, Any>>{
        val urlDataString = Api.fetchJsonData("http://10.0.2.2:8080/api/transaction")
        return Api.parseJsonArray(urlDataString)
    }

}