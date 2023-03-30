package cryogenetics.logistics.ui.inventory


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.databinding.InventoryRecyclerItemBinding

class InventoryAdapter(
    private val mOnProductClickListener: AdapterView.OnItemClickListener,
    private val mProductList: ArrayList<InventoryDataModel> = ArrayList()
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binder: InventoryRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var _binding: InventoryRecyclerItemBinding? = null
    private val binding get() = _binding!!

    /**
     * ViewHolder implementation for holding the mapped views.
     */
    /*inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val inventoryNr: TextView = binding.tvInventoryNr
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  {
        val binder = InventoryRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        _binding = binder

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // data will be set here whenever the system thinks it's required

        // get the product at position
        val product = mProductList[position]

        binding.tvInventoryNr.text = "ONEEE"
        binding.tvInventoryLocation.text = "TWOO"
        binding.tvInventoryClient.text = "THREE"
        binding.tvInventoryLastFill.text = "FOUUR"
        binding.tvInventoryStatus.text = "FIVE"
        binding.tvInventoryInvoice.text = "SIX"
        binding.tvInventoryNoti.text = "SEVEN"
    }

    /**
     * Returns the total number of items in the list to be displayed.
     * this will refresh when we call notifyDataSetChanged() or other related methods.
     */
    override fun getItemCount(): Int {
        return mProductList.size
    }

    /**
     * Adds each item to list for recycler view.
     */
    fun addProduct(model: InventoryDataModel) {
        mProductList.add(model)
        notifyItemInserted(mProductList.size)
    }

    /**
     * Updates the existing product at specific position of the list.
     */
    fun updateProduct(model: InventoryDataModel?) {
        if (model == null) return // we cannot update the value because it is null
        for (item in mProductList) {
            // search by id
            if (item.id == model.id) {
                val position = mProductList.indexOf(model)
                mProductList[position] = model
                notifyItemChanged(position)
                break // Stops the loop
            }
        }
    }

    /**
     * Removes the specified product from the list.
     *
     * @param model to be removed
     */
    fun removeProducts(clearAll: Boolean) {
        val mDeleteList: ArrayList<InventoryDataModel> = ArrayList()

        if (mProductList.isNotEmpty()) {
            for (item in mProductList) {
                if (clearAll) {
                    mDeleteList.add(item)
                } else {
                    if (item.isChecked) {
                        item.isChecked = false
                        mDeleteList.add(item)
                    }
                }
            }
            for (item in mDeleteList) {
                val position = mProductList.indexOf(item)
                mProductList.remove(item)
                notifyItemRemoved(position)
            }
        }
    }


    fun getNextItemId(): Int {
        var id = 1
        if (mProductList.isNotEmpty()) {
            // .last is equivalent to .size() - 1
            // we want to add 1 to that id and return it
            id = mProductList.last().id + 1
        }
        return id
    }
}
