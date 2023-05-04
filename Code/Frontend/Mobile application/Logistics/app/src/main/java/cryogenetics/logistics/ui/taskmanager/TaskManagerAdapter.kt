package cryogenetics.logistics.ui.taskmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 *  Controls interactions with the task manager/bar.
 */
class TaskManagerAdapter(
    var dataList: List<TaskItem>,           // Initial list of tabs.
    val onClick: (Fragment, Int) -> Unit,   // On click (each tab)
    val onClickX: (Fragment, Int) -> Unit   // On click (X-button for each tab)
): RecyclerView.Adapter<TaskManagerAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvName: TextView
        val ivX: ImageView
        val ivCur: ImageView
        val constL: ConstraintLayout

        init {
            tvName = view.findViewById(cryogenetics.logistics.R.id.taskItemtv)
            ivX = view.findViewById(cryogenetics.logistics.R.id.taskItemX)
            ivCur = view.findViewById(cryogenetics.logistics.R.id.ivCurTaskItem)
            constL = view.findViewById(cryogenetics.logistics.R.id.clTaskItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(cryogenetics.logistics.R.layout.task_item, parent, false)
        return  ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // Set up behavior of tabs
        val item = dataList[holder.adapterPosition]
        holder.tvName.text = item.name
        holder.ivCur.setImageResource(item.picRef)

        holder.constL.setOnClickListener {
            onClick(item.fragment, holder.adapterPosition)
        }

        holder.ivX.setOnClickListener {
            onClickX(item.fragment, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
    }

    /**
     *  Updates the adapter with new data.
     */
    fun updateData(updatedDataList: List<TaskItem>){
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}