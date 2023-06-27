package com.example.eventify.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.R
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityAdapter(
    private var items: List<Activity>,
    private val listener: OnItemClickListener,
    private val context: Context
) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(activity: Activity)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.event_title)
        val eventDateTime: TextView = itemView.findViewById(R.id.event_datetime)
        val eventLocation: TextView = itemView.findViewById(R.id.event_location)
        val eventCategory: Chip = itemView.findViewById(R.id.event_category)
        val layout: View = itemView

        init {
            itemView.setOnClickListener {
                listener.onItemClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.event_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.eventTitle.text = item.name
        holder.eventDateTime.text = item.time
        holder.eventLocation.text = item.location
        var categoryName = EventifyDatabase.getInstance(context).getCategoryDao()
            .getCategoryNameById(item.categoryId)
        holder.eventCategory.text = categoryName
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newActivities: List<Activity>) {
        items = newActivities
        notifyDataSetChanged()
    }
}
