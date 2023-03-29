package cryogenetics.logistics.ui.taskmanager

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.TestFragmentA.TestFragmentA
import cryogenetics.logistics.ui.TestFragmentB.TestFragmentB

/**
 *  Contains and creates task manager/bar view.
 */
class TaskManagerFragment : Fragment() {

    companion object {
        fun newInstance() = TaskManagerFragment()
    }

    private lateinit var viewModel: TaskManagerViewModel
    private lateinit var mAdapter: TaskManagerAdapter
    private lateinit var mImgButton1: ImageView
    private lateinit var mImgButton2: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adapter
        var taskManagerData: List<Fragment> = mutableListOf<Fragment>(
        )

        mAdapter = TaskManagerAdapter(taskManagerData,
            // OnClick
            { _, i ->
                // Switch to fragment
                childFragmentManager.beginTransaction()
                    .replace(R.id.taskManager, mAdapter.dataList[i])
                    .commit()
            },

            // OnClickX
            {_, i ->
                // Remove fragment from hierarchy
                childFragmentManager.beginTransaction()
                    .remove(mAdapter.dataList[i])
                    .commit()

                // Remove fragment from data list
                var data = mAdapter.dataList.toMutableList()
                data.removeAt(i)
                mAdapter.updateData(data)
            }
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.rvTaskList)
            ?: return
        recyclerView.adapter = mAdapter

        mAdapter.updateData(taskManagerData)

        // Find components
        mImgButton1 = view.findViewById(R.id.imagebutton1)
        mImgButton2 = view.findViewById(R.id.imagebutton2)

        // Attach listeners
        mImgButton1.setOnClickListener {
            openTestFragmentA(view)
        }

        mImgButton2.setOnClickListener {
            openTestFragmentB()
        }
    }

    fun openTestFragmentA(view: View) {
        // Create a test fragment and expand it
        val childFragment: Fragment = TestFragmentA()
        childFragmentManager.beginTransaction()
            .replace(R.id.taskManager, childFragment)
            .commit()

        // Add it to the list of fragments
        var data = mAdapter.dataList.toMutableList()
        data.add(childFragment)
        mAdapter.updateData(data)

        /* IN CASE THE FRAGMENT MUST BE OPENED BY THE PARENT ACTIVITY
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(cryogenetics.logistics.R.id.hostFragment, childFragment)
            ?.commit()*/
    }

    fun openTestFragmentB() {
        // Create a test fragment and expand it
        val childFragment: Fragment = TestFragmentB()
        childFragmentManager.beginTransaction()
            .replace(R.id.taskManager, childFragment)
            .commit()

        // Add it to the list of fragments
        var data = mAdapter.dataList.toMutableList()
        data.add(childFragment)
        mAdapter.updateData(data)

        /* IN CASE THE FRAGMENT MUST BE OPENED BY THE PARENT ACTIVITY
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(cryogenetics.logistics.R.id.hostFragment, childFragment)
            ?.commit()*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TaskManagerViewModel::class.java)
    }

}