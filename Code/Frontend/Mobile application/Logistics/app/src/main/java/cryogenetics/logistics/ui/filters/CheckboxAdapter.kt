package cryogenetics.logistics.ui.filters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R

/**
 *  Adapter for filter lists mainly consisting of checkboxes.
 */
class CheckboxAdapter(
    var dataList: List<Pair<String, Boolean>>//,           // Initial list of tabs.
    //val onClick: (String, Int) -> Unit,   // On click (each tab)
): RecyclerView.Adapter<CheckboxAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val mCheckBox: CheckBox

        init {
            mCheckBox = view.findViewById(R.id.cbFilterItem)
        }
    }

    // Checkbox states
    private val checkboxStates = mutableMapOf<String, Boolean>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkbox_item, parent, false)
        return  ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Set up behavior of checkboxes
        val state = dataList[holder.adapterPosition]
        holder.mCheckBox.text = state.first
        holder.mCheckBox.isChecked = state.second

        // Keep track of checkbox states
        checkboxStates[state.first] = state.second
        holder.mCheckBox.setOnClickListener {
            checkboxStates[state.first] = holder.mCheckBox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     *  Gets the states of the checkboxes.
     *
     *  @return The states of the checkboxes.
     */
    fun getCheckboxStates() : Map<String, Boolean> {
        return checkboxStates
    }

    /**
     *  Gets the states of the checkboxes.
     *
     *  @return The states of the checkboxes.
     */
    fun getCheckboxStatesAsString() : Map<String, String> {
        return checkboxStates.map { (key, value) -> key to value.toString() }.toMap()
    }

    /**
     *  Updates the adapter with new data.
     */
    fun updateData(updatedDataList: List<Pair<String, Boolean>>){
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}