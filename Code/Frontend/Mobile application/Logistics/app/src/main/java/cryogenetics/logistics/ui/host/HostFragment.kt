package cryogenetics.logistics.ui.host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.databinding.FragmentHostBinding
import cryogenetics.logistics.databinding.FragmentInventoryBinding
import cryogenetics.logistics.ui.actLog.ActLogFragment
import cryogenetics.logistics.ui.inventory.InventoryFragment
import cryogenetics.logistics.ui.tank.TankFragment
import cryogenetics.logistics.ui.taskmanager.TaskItem
import cryogenetics.logistics.ui.taskmanager.TaskManagerAdapter

class HostFragment : Fragment() {

    companion object {
        fun newInstance() = HostFragment()
    }

    private var _binding : FragmentHostBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HostViewModel
    private lateinit var mAdapter: TaskManagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adapter
        var taskManagerData: List<TaskItem> = mutableListOf<TaskItem>()

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
                var data = mAdapter.dataList.toMutableList()
                data.removeAt(index)
                mAdapter.updateData(data)
            }
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.rvTaskList)
            ?: return
        recyclerView.adapter = mAdapter
        mAdapter.updateData(taskManagerData)

        // Find components
        //mIvDashboard    = view.findViewById(cryogenetics.logistics.R.id.ivDashboard)

        // Set onclick listeners
        binding.clDashboard.setOnClickListener {
            openAndAddFragment(TankFragment(), "Dashboard")
        }
        binding.clTank.setOnClickListener {
            openAndAddFragment(TankFragment(), "Tank")
        }
        binding.clTankFilling.setOnClickListener {
            openAndAddFragment(TankFragment(), "Tank Filling")
        }
        binding.clLog.setOnClickListener {
            openAndAddFragment(ActLogFragment(), "Log")
        }
        binding.clInventory.setOnClickListener {
            openAndAddFragment(InventoryFragment(), "Inventory")
        }

    }

    /**
     *  Opens a fragment by replacing the current one with it and adds to taskmanager.
     *
     *  @param fragment - The fragment.
     *  @param name - The name to display on the tab.
     */
    fun openAndAddFragment(fragment: Fragment, name: String) {
        // Create the fragment and replace the current one with it
        childFragmentManager.beginTransaction()
            .replace(R.id.mainContent, fragment)
            .commit()

        // Add it to the list of fragments
        var data = mAdapter.dataList.toMutableList()
        data.add(TaskItem(name, fragment))
        mAdapter.updateData(data)

        /* IN CASE THE FRAGMENT MUST BE OPENED BY THE PARENT ACTIVITY
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(cryogenetics.logistics.R.id.hostFragment, childFragment)
            ?.commit()*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HostViewModel::class.java)
    }

}