package cryogenetics.logistics.ui.host

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.dataStore
import cryogenetics.logistics.databinding.FragmentHostBinding
import cryogenetics.logistics.functions.Functions
import cryogenetics.logistics.ui.actLog.ActLogFragment
import cryogenetics.logistics.ui.inventory.InventoryFragment
import cryogenetics.logistics.ui.dash.DashFragment
import cryogenetics.logistics.ui.tank.TankFragment
import cryogenetics.logistics.ui.tankfill.TankFillFragment
import cryogenetics.logistics.ui.taskmanager.TaskItem
import cryogenetics.logistics.ui.taskmanager.TaskManagerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class HostFragment : Fragment() {

    companion object {
        fun newInstance() = HostFragment()

        /**
         *  Opens a fragment by replacing the current one with it and adds to taskmanager.
         *
         *  @param fragment - The fragment.
         *  @param name - The name to display on the tab.
         */
        fun openAndAddFragment(hostFragment: HostFragment, fragment: Fragment, name: String, picRef: Int) {
            // Create the fragment and replace the current one with it
            hostFragment.childFragmentManager.beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit()

            // Add it to the list of fragments
            val data = hostFragment.mAdapter.dataList.toMutableList()
            data.add(TaskItem(name, fragment, picRef))
            hostFragment.mAdapter.updateData(data)

            /* IN CASE THE FRAGMENT MUST BE OPENED BY THE PARENT ACTIVITY
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(cryogenetics.logistics.R.id.hostFragment, childFragment)
                ?.commit()*/
        }
        fun returnHostFragment (): HostFragment {
            return xHostFragment
        }

        // We will allow this to have the option to open tabs like a complete act log of a tank.
        @SuppressLint("StaticFieldLeak")
        lateinit  var xHostFragment: HostFragment
    }

    private var _binding : FragmentHostBinding? = null
    private val binding get() = _binding!!

    private lateinit var mAdapter: TaskManagerAdapter
    private lateinit var tvUsername: TextView
    private lateinit var ibLogout: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        xHostFragment = this

        // Fetch components
        tvUsername = view.findViewById(R.id.tvUsername)
        ibLogout = view.findViewById(R.id.ibLogout)

        // Set username text
        val key = stringPreferencesKey("employee_name")
        val flow: Flow<String> = requireContext().dataStore.data
            .map {
                it[key] ?: "No name found"
            }
        runBlocking (Dispatchers.IO) {
            tvUsername.text = flow.first()
        }

        // Set logout onclick
        ibLogout.setOnClickListener {
            Functions.restartApp(requireContext(), null)
        }

        // Set up adapter
        val taskManagerData: List<TaskItem> = mutableListOf<TaskItem>()

        mAdapter = TaskManagerAdapter(taskManagerData,
            // OnClick
            { fragment, _ ->
                // Switch to fragment
                childFragmentManager.beginTransaction()
                    .replace(R.id.mainContent, fragment)
                    .commit()
            },

            // OnClickX
            {fragment, index ->
                // Remove fragment from hierarchy
                childFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commit()

                // Remove fragment from data list
                val data = mAdapter.dataList.toMutableList()
                data.removeAt(index)
                mAdapter.updateData(data)
            }
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.rvTaskList)
            ?: return
        recyclerView.adapter = mAdapter
        mAdapter.updateData(taskManagerData)

        // Set onclick listeners
        binding.clDashboard.setOnClickListener {
            Companion.openAndAddFragment(this, DashFragment(), "Dashboard", R.drawable.dashboard)
        }
        binding.clTank.setOnClickListener {
            Companion.openAndAddFragment(this, TankFragment(), "Tank", R.drawable.tank)
        }
        binding.clTankFilling.setOnClickListener {
            Companion.openAndAddFragment(this, TankFillFragment(), "Tank Filling", R.drawable.fill)
        }
        binding.clLog.setOnClickListener {
            Companion.openAndAddFragment(this, ActLogFragment(), "Log", R.drawable.recent_transactions)
        }
        binding.clInventory.setOnClickListener {
            Companion.openAndAddFragment(this, InventoryFragment(), "Inventory", R.drawable.inventory)
        }
    }
}