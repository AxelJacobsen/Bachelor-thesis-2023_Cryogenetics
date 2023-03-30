package cryogenetics.logistics.ui.actLog

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import cryogenetics.logistics.databinding.FragmentInventoryBinding
import cryogenetics.logistics.ui.inventory.ActLogViewModel
import java.util.*

class ActLogFragment : Fragment() {

    companion object {
        fun newInstance() = ActLogFragment()
    }

    private var _binding : FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    //private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: ActLogViewModel
    private lateinit var mProductListAdapter: ActLogAdapter

    private var modelToBeUpdated: Stack<ActLogDataModel> = Stack()

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
            TODO("Not yet implemented")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.tvInventoryNr.text = "TESTING123"

        // initialize the recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

        // initialize the recyclerView-adapter
        mProductListAdapter = ActLogAdapter(mOnProductClickListener/*, arrayListOf(InventoryDataModel())*/)
        binding.recyclerView.adapter = mProductListAdapter

        val model = ActLogDataModel(0, "name", true)
        mProductListAdapter.addProduct(model)
        mProductListAdapter.addProduct(model)
        mProductListAdapter.addProduct(model)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ActLogViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}