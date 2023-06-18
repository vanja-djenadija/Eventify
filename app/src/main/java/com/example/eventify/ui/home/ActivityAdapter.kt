package com.example.eventify.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventify.R
import com.example.eventify.db.model.Activity

class ActivityAdapter(private val items: List<Activity>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(activity: Activity)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventTitle: TextView = itemView.findViewById(R.id.event_title)
        val eventDateTime: TextView = itemView.findViewById(R.id.event_datetime)
        val eventLocation: TextView = itemView.findViewById(R.id.event_location)
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
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
