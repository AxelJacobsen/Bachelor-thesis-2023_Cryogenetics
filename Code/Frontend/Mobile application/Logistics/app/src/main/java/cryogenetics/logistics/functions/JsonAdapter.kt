package cryogenetics.logistics.functions

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cryogenetics.logistics.R
import cryogenetics.logistics.ui.tank.OnItemClickListener
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class JsonAdapter(
    var itemList: MutableList<Map<String, Any>>,
    private val viewIds: List<Int>,
    private val recyclerItem: Int,
    private val listener: OnItemClickListener? = null,
    private val tvActLogRNrVisible: Boolean = false,
    private val tvInventoryClient: Boolean = false,
) : RecyclerView.Adapter<JsonAdapter.ViewHolder>() {


    class ViewHolder(view: View, viewIds: List<Int>) : RecyclerView.ViewHolder(view) {
        //Prepare vessel to hold all TextViews
        val views: MutableMap<Int, TextView> = mutableMapOf()

        val imageView: ImageView? = view.findViewById<ImageView>(R.id.ivInventoryLastFill)

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

        if (tvInventoryClient) view.findViewById<TextView>(R.id.tvInventoryClient).visibility =
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

        if (holder.imageView != null) {
            //holder.imageView.setImageResource(R.drawable.battery50)
            val refillNumb =
                itemList[position].entries.find { it.key == "refill_interval" }?.value.toString()
            val lastFilled =
                itemList[position].entries.find { it.key == "last_filled" }?.value.toString()

            val dateDiff = dateDifference(lastFilled)
            if (dateDiff < 1) {
                holder.imageView.setImageResource(R.drawable.battery100)
            } else {
                val percentDiff = 1 - (dateDiff.toFloat() / refillNumb.toFloat())

                if (percentDiff >= 0.95) {
                    holder.imageView.setImageResource(R.drawable.battery100)
                } else if (percentDiff in 0.85..0.95) {
                    holder.imageView.setImageResource(R.drawable.battery90)
                } else if (percentDiff in 0.75..0.85) {
                    holder.imageView.setImageResource(R.drawable.battery80)
                } else if (percentDiff in 0.65..0.75) {
                    holder.imageView.setImageResource(R.drawable.battery70)
                } else if (percentDiff in 0.55..0.65) {
                    holder.imageView.setImageResource(R.drawable.battery60)
                } else if (percentDiff in 0.45..0.55) {
                    holder.imageView.setImageResource(R.drawable.battery50)
                } else if (percentDiff in 0.35..0.45) {
                    holder.imageView.setImageResource(R.drawable.battery40)
                } else if (percentDiff in 0.25..0.35) {
                    holder.imageView.setImageResource(R.drawable.battery30)
                } else if (percentDiff in 0.15..0.25) {
                    holder.imageView.setImageResource(R.drawable.battery20)
                } else {
                    holder.imageView.setImageResource(R.drawable.battery10)
                }
            }
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
            //imageView.setImageResource(R.drawable.battery50)
            //Finally find correct json data and fill textview
            val text = item[tTag]?.toString() ?: ""
            textView.text = text
            if (tTag == "apercentDiffress") {
                if (text == "" || text == "null") {
                    textView.text =
                        itemList[position].entries.find { it.key == "location_name" }?.value.toString()
                    item[tTag] = textView.text
                }
            }
        }
    }

    /**
     * Get date difference between a date-string and current device date.
     */
    private fun dateDifference(lastFilled: String): Long {
        if (lastFilled == "0000-00-00" || lastFilled == "null") // Ensures that formatter doesn't fail if date = 00...
            return 99

        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val from = LocalDate.parse(lastFilled, dateFormatter)
        val to = LocalDate.parse(Functions.getDate().toString(), dateFormatter)

        return ChronoUnit.DAYS.between(from, to)
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

