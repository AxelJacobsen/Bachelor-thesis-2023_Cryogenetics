package cryogenetics.logistics.ui.inventory

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class InventoryFragment : Fragment() {

    private lateinit var binding : InventoryFragment
    private lateinit var itemList: RecyclerView
    private lateinit var mProductListAdapter: InventoryAdapter


    private var recyclerView: RecyclerView? = null
    private var paymentData: ArrayList<InventoryDataModel>? = null

    companion object {
        fun newInstance() = InventoryFragment()
    }

    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        
        return inflater.inflate(cryogenetics.logistics.R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initialize the recyclerView
        itemList = R.layout.inventory_recycler_item
        itemList.layoutManager = LinearLayoutManager(this)
        itemList.setHasFixedSize(true)

        // initialize the recyclerView-adapter
        mProductListAdapter = ItemListAdapter(this, mOnProductClickListener)
        itemList.adapter = mProductListAdapter
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(InventoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}