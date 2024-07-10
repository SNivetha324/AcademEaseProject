package com.ac.maduidesigns.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.Model.eventsmodel
import com.ac.maduidesigns.R

class eventadapter(private val eventsList: ArrayList<eventsmodel>) : RecyclerView.Adapter<eventadapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val venueTextView: TextView = itemView.findViewById(R.id.venueTextView)
        val cityTextView: TextView = itemView.findViewById(R.id.cityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eventscard, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]

        holder.titleTextView.text = event.title
        holder.descriptionTextView.text = event.description
        holder.dateTextView.text = event.date
        holder.timeTextView.text = event.time
        holder.venueTextView.text = event.venue
        holder.cityTextView.text = event.city
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    // In your adapter class, add a method to update the data
    fun setData(eventsList: List<eventsmodel>) {
        this.eventsList.clear()
        this.eventsList.addAll(eventsList)
        notifyDataSetChanged()
    }


}
