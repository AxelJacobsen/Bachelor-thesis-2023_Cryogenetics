package cryogenetics.logistics.ui.filters

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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

    private lateinit var etDateStart: EditText
    private lateinit var etDateEnd: EditText

    private lateinit var rvFilterStatus: RecyclerView
    private lateinit var rvFilterAct: RecyclerView
    private lateinit var rvFilterLocations: RecyclerView
    private lateinit var rvFilterSize: RecyclerView

    private var adapters: MutableMap<View, FilterAdapter> = mutableMapOf()
    private var shorthands: MutableMap<View, String> = mutableMapOf()

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

        etDateStart = view.findViewById(R.id.etDateStart)
        etDateEnd = view.findViewById(R.id.etDateEnd)

        rvFilterStatus      = view.findViewById(R.id.rvFilterStatus)
        rvFilterAct         = view.findViewById(R.id.rvFilterAct)
        rvFilterLocations   = view.findViewById(R.id.rvFilterLocations)
        rvFilterSize        = view.findViewById(R.id.rvFilterSize)

        // Add recyclerViews & attach adapters
        addEditText(etDateStart, "start_date")
        addEditText(etDateEnd, "end_date")
        addRecyclerview(rvFilterStatus, "container_status_name")
        addRecyclerview(rvFilterAct, "act")
        addRecyclerview(rvFilterLocations, "location_name")
        addRecyclerview(rvFilterSize, "liter_capacity")

        // On reset
        bFilterReset.setOnClickListener {
            for (v in shorthands) {
                when (v.key) {
                    is RecyclerView -> {
                        val adapter = adapters[v.key]
                        adapter?.updateData(adapter.dataList.map { Pair(it.first, false) })
                    }
                    is EditText -> {
                        (v.key as EditText).setText("")
                    }
                }
            }
        }

        // On apply
        bFilterApply.setOnClickListener {
            onApply( getFilterState() )
        }
    }

    /**
     *  Adds a recyclerView to the list of views and gives it an adapter.
     *
     *  @param recyclerView - The recyclerView.
     *  @param shorthand - The shorthand or "name" of the recyclerView.
     */
    private fun addRecyclerview(recyclerView: RecyclerView, shorthand: String) {
        shorthands[recyclerView] = shorthand
        val adapter = FilterAdapter(initialState[shorthand]?.toList()?.map { it.first to (it.second=="true")} ?: emptyList())
        adapters[recyclerView] = adapter
        recyclerView.adapter = adapter
    }

    /**
     *  Adds an editText to the list of views.
     *
     *  @param editText - The editText.
     *  @param shorthand - The shorthand or "name" of the editText.
     */
    private fun addEditText(editText: EditText, shorthand: String) {
        shorthands[editText] = shorthand
        editText.setText(initialState[shorthand]?.get("text") ?: "")
    }

    /**
     *  Gets the current filter state.
     *  The filter state is in the format "shorthand" : ("subElementName" : "true"/"false"/other).
     *
     *  @return The current filter state.
     */
    private fun getFilterState() : Map<String, Map<String, String>> {
        val filterStates = shorthands.map {
            it.value to ( when (it.key) {
                is RecyclerView -> adapters[it.key]?.getCheckboxStatesAsString() ?: emptyMap<String, String>()
                is EditText -> mapOf("text" to (it.key as EditText).text.toString())
                else -> emptyMap<String, String>()
            } ) as Map<String, String>
        }.toMap()
        return filterStates
    }
}