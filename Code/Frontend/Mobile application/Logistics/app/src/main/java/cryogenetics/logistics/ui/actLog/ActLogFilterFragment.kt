package cryogenetics.logistics.ui.actLog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.filters.FilterManager

/**
 * A [Fragment] subclass for storing filter lists.
 */
class ActLogFilterFragment (
    var onApply: (Map<String, Map<String, String>>) -> Unit,
    private val filterManager: FilterManager
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = inflater.inflate(R.layout.fragment_act_log_filter, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch views
        bFilterReset    = view.findViewById(R.id.bActLogFilterReset)
        bFilterApply    = view.findViewById(R.id.bActLogFilterApply)

        etDateStart = view.findViewById(R.id.etActLogDateStart)
        etDateEnd = view.findViewById(R.id.etActLogDateEnd)

        rvFilterStatus      = view.findViewById(R.id.rvActLogFilterStatus)
        rvFilterAct         = view.findViewById(R.id.rvActLogFilterAct)
        rvFilterLocations   = view.findViewById(R.id.rvActLogFilterLocations)
        rvFilterSize        = view.findViewById(R.id.rvActLogFilterSize)

        // Add recyclerViews & attach adapters
        filterManager.addTable("start_date", etDateStart)
        filterManager.addTable("end_date", etDateEnd)
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/container_status", "container_status_name", rvFilterStatus, listOf("container_status_name"))
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/act", "act", rvFilterAct, listOf("act_name"))
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/location", "location_name", rvFilterLocations, listOf("location_name"))
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/container_model", "liter_capacity", rvFilterSize, listOf("liter_capacity"))

        // On reset
        bFilterReset.setOnClickListener {
            filterManager.reset()
        }

        // On apply
        bFilterApply.setOnClickListener {
            onApply( filterManager.getState() )
        }
    }
}