package cryogenetics.logistics.ui.inventory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.databinding.FragmentInventoryBinding
import java.util.*

class InventoryFragment : Fragment() {

    companion object {
        fun newInstance() = InventoryFragment()
    }

    private var _binding : FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    //private lateinit var inventoryList: RecyclerView
    private lateinit var viewModel: InventoryViewModel
    private lateinit var mProductListAdapter: InventoryAdapter

    private var modelToBeUpdated: Stack<InventoryDataModel> = Stack()

    private val mOnProductClickListener = object : AdapterView.OnItemClickListener {
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
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            TODO("Not yet implemented")
        }

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
        binding.HeaderPayment?.text = "TESTING123"

        // initialize the recyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)

        // initialize the recyclerView-adapter
        mProductListAdapter = InventoryAdapter(mOnProductClickListener/*, arrayListOf(InventoryDataModel())*/)
        binding.recyclerView.adapter = mProductListAdapter

        val model = InventoryDataModel(0, "name", true)
        mProductListAdapter.addProduct(model)
        mProductListAdapter.addProduct(model)
        mProductListAdapter.addProduct(model)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}