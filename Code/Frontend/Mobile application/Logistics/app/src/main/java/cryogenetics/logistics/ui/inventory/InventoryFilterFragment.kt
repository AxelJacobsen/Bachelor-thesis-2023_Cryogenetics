package cryogenetics.logistics.ui.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.filters.FilterManager

class InventoryFilterFragment (
    var onApply: (Map<String, Map<String, String>>) -> Unit,
    private val filterManager: FilterManager
    ) : Fragment() {
    lateinit var binding: View

    // Views and components
    private lateinit var bFilterReset: Button
    private lateinit var bFilterApply: Button

    private lateinit var rvFilterSize: RecyclerView
    private lateinit var rvFilterStatus: RecyclerView
    private lateinit var rvFilterClient: RecyclerView
    private lateinit var rvFilterLocations: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = inflater.inflate(R.layout.fragment_inventory_filter, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch views
        bFilterReset    = view.findViewById(R.id.bInventoryFilterReset)
        bFilterApply    = view.findViewById(R.id.bInventoryFilterApply)

        rvFilterSize        = view.findViewById(R.id.rvInventoryFilterSize)
        rvFilterStatus      = view.findViewById(R.id.rvInventoryFilterStatus)
        rvFilterClient         = view.findViewById(R.id.rvInventoryFilterClient)
        rvFilterLocations   = view.findViewById(R.id.rvInventoryFilterLocations)

        // Add recyclerViews & attach adapters
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/container_model", "liter_capacity", rvFilterSize, listOf("liter_capacity"))
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/container_status", "container_status_name", rvFilterStatus, listOf("container_status_name"))
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/client", "client_name", rvFilterClient, listOf("client_name"))
        filterManager.addTableFromDB("http://10.0.2.2:8080/api/location", "location_name", rvFilterLocations, listOf("location_name"))

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