package cryogenetics.logistics.ui.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 *  Controls interactions with the task manager/bar.
 */
class TaskManagerAdapter(var dataList: List<Fragment>, val onClick:(Fragment, Int)->Unit): RecyclerView.Adapter<TaskManagerAdapter.ItemViewHolder>() {
    // Create bindings for clicked object
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvTest: TextView
        init {
            tvTest = view.findViewById(cryogenetics.logistics.R.id.taskItemtv)
        }
    }

    // Creates a viewholder to wrap recieved items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(cryogenetics.logistics.R.layout.task_item, parent, false)
        return  ItemViewHolder(view)
    }

    // When clicked, returns the specific item's data to clicker
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataList[holder.adapterPosition]
        holder.tvTest.text = "YO!"

        holder.tvTest.setOnClickListener { _ ->
            onClick(item, holder.adapterPosition)
        }
    }

    // Returns item count
    override fun getItemCount(): Int {
        return  dataList.size
    }

    /**
     *  Updates the adapter with new data.
     */
    fun updateData(updatedDataList: List<Fragment>){
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}