package cryogenetics.logistics.ui.filters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import java.security.KeyStore.Entry

/**
 * A [Fragment] subclass for storing filter lists.
 */
class FilterFragment (
    var onApply: (Map<String, Map<String, String>>) -> Unit,
    var initialState: Map<String, Map<String, String>>
) : Fragment() {
    lateinit var binding: View

    // Views and components
    private lateinit var bFilterReset: Button
    private lateinit var bFilterApply: Button

    private lateinit var rvFilterStatus: RecyclerView
    private lateinit var rvFilterAct: RecyclerView
    private lateinit var rvFilterLocations: RecyclerView
    private lateinit var rvFilterSize: RecyclerView

    private var adapters: MutableMap<RecyclerView, FilterAdapter> = mutableMapOf()
    private var shorthands: MutableMap<RecyclerView, String> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = inflater.inflate(R.layout.fragment_filter, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch views
        bFilterReset    = view.findViewById(R.id.bFilterReset)
        bFilterApply    = view.findViewById(R.id.bFilterApply)

        rvFilterStatus      = view.findViewById(R.id.rvFilterStatus)
        rvFilterAct         = view.findViewById(R.id.rvFilterAct)
        rvFilterLocations   = view.findViewById(R.id.rvFilterLocations)
        rvFilterSize        = view.findViewById(R.id.rvFilterSize)

        // Add recyclerViews & attach adapters
        addRecyclerview(rvFilterStatus, "status")
        addRecyclerview(rvFilterAct, "act")
        addRecyclerview(rvFilterLocations, "locations")
        addRecyclerview(rvFilterSize, "size")

        // On reset
        bFilterReset.setOnClickListener {
            for (adapter in adapters)
                adapter.value.updateData(adapter.value.dataList.map { Pair(it.first, false) })
        }

        // On apply
        bFilterApply.setOnClickListener {
            onApply( getFilterState() )
        }
    }

    /**
     *  Adds a recyclerView to the list of recyclerViews and gives it an adapter.
     *
     *  @param recyclerView - The recyclerView.
     *  @param shorthand - The shorthand or "name" of the recyclerView.
     */
    private fun addRecyclerview(recyclerView: RecyclerView, shorthand: String) {
        shorthands[recyclerView] = shorthand
        val adapter = FilterAdapter(
            initialState[shorthand]?.toList()?.map { it.first to (it.second=="true")} ?: emptyList()
        )
        adapters[recyclerView] = adapter
        recyclerView.adapter = adapter
    }

    /**
     *  Gets the current filter state.
     *  The filter state is in the format "filterName" : ("checkboxName" : "true"/"false").
     *
     *  @return The current filter state.
     */
    private fun getFilterState() : Map<String, Map<String, String>> {
        return shorthands.map { it.value to (adapters[it.key]?.getCheckboxStatesAsString() ?: emptyMap()) }.toMap()
    }
}