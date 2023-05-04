package cryogenetics.logistics.ui.tankfill

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.tank.OnItemClickListener

class TankFillAdapter(
    private val itemList: MutableList<Map<String, Any>>,
    private val viewIds: List<Int>,
    private val mOnProductClickListener: OnItemClickListener
) : RecyclerView.Adapter<TankFillAdapter.ViewHolder>() {

    class ViewHolder(view: View, viewIds: List<Int>) : RecyclerView.ViewHolder(view) {
        //Prepare vessel to hold all TextViews
        val views: MutableMap<Int, TextView> = mutableMapOf()

        init {
            //Propagate views with textViews based on their ID provided by the fragment
            for (viewId in viewIds) {
                val textView = view.findViewById<TextView>(viewId)
                //If views are filled with nulls it breaks
                if (textView != null){
                    views[viewId] = textView
                } else {
                    Log.e(TAG, "No textView found with id: $viewId")
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create a view based on the parent viewgroup
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tank_fill_recycler, parent, false)
        return ViewHolder(view, viewIds)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        val itemCB: CheckBox = holder.itemView.findViewById(R.id.cbRefilledTanks)
        val bComment: Button = holder.itemView.findViewById(R.id.bComment)
        val bDetails: Button = holder.itemView.findViewById(R.id.bDetails)
        itemCB.isChecked = false

        itemCB.setOnCheckedChangeListener { _, isChecked ->
            mOnProductClickListener.onChecked(itemList[position], isChecked)
        }
        bComment.setOnClickListener {
            mOnProductClickListener.onClickTankFill(itemList[holder.adapterPosition], "bComment")
        }
        bDetails.setOnClickListener {
            mOnProductClickListener.onClickTankFill(itemList[holder.adapterPosition], "bDetails")
        }
        //Iterate the view list constructed in above function
        for ((viewId, textView) in holder.views) {
            // Normally viewId would be used, however it doesn't match the json data we receive
            // This is why we use the tag system to find the objects we want in the json data
            // There probably is a better way, talk to Axel if you have suggestions
            val tTag = textView.tag as? String
            if (tTag == null){
                //Logs an error for development, isn't critical for function as text will be empty
                Log.e(TAG, "Couldnt find textview tag")
            }
            //Finally find correct json data and fill textview
            val text = item[tTag]?.toString() ?: ""
            textView.text = text

        }
    }

    fun updateData(model: Map<String, Any>) {
        itemList.add(model)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}