package cryogenetics.logistics.functions

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.tank.OnItemClickListener

class JsonAdapter(
    var itemList: MutableList<Map<String, Any>>,
    private val viewIds: List<Int>,
    private val recyclerItem: Int,
    private val listener: OnItemClickListener? = null,
    private val tvActLogRNrVisible: Boolean = false,
) : RecyclerView.Adapter<JsonAdapter.ViewHolder>() {

    class ViewHolder(view: View, viewIds: List<Int>) : RecyclerView.ViewHolder(view) {
        //Prepare vessel to hold all TextViews
        val views: MutableMap<Int, TextView> = mutableMapOf()

        init {
            //Propagate views with textViews based on their ID provided by the fragment
            for (viewId in viewIds) {
                val textView = view.findViewById<TextView>(viewId)
                //If views are filled with nulls it breaks
                if (textView != null) {
                    views[viewId] = textView
                } else {
                    Log.e(TAG, "No textView found with id: $viewId")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Create a view based on the parent viewgroup
        val view = LayoutInflater.from(parent.context).inflate(recyclerItem, parent, false)
        if (tvActLogRNrVisible) view.findViewById<TextView>(R.id.tvActLogRNr).visibility =
            View.VISIBLE

        return ViewHolder(view, viewIds)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position].toMutableMap()

        // ItemList sends map of position when clicked.
        if (listener != null)
            holder.itemView.setOnClickListener {
                listener.onClick(itemList[position]) // Sends the map according to pos
            }

        //Iterate the view list constructed in above function
        for ((viewId, textView) in holder.views) {
            // Normally viewId would be used, however it doesn't match the json data we receive
            // This is why we use the tag system to find the objects we want in the json data
            // There probably is a better way, talk to Axel if you have suggestions
            val tTag = textView.tag as? String
            if (tTag == null) {
                //Logs an error for development, isn't critical for function as text will be empty
                Log.e(TAG, "Couldnt find textview tag")
            }
            //Finally find correct json data and fill textview
            val text = item[tTag]?.toString() ?: ""
            textView.text = text
            if (tTag == "address") {
                if (text == "" || text == "null") {
                    textView.text =
                        itemList[position].entries.find { it.key == "location_name" }?.value.toString()
                    item[tTag] = textView.text
                }
            }
        }
    }

    /**
     * Gets item count by itemList.size instead of default.
     * @return The nr of items in itemList
     */
    override fun getItemCount(): Int {
        return itemList.size
    }

    /**
     * Updates data in the list.
     * @param newData - The data of the new list.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Map<String, Any>>) {
        itemList = newData as MutableList<Map<String, Any>>
        notifyDataSetChanged()
    }
}