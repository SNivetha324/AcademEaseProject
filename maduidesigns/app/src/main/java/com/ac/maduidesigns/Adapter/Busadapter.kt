package com.ac.maduidesigns.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.Model.Bus
import com.ac.maduidesigns.R

class Busadapter(private var busList: List<Bus>) : RecyclerView.Adapter<Busadapter.BusViewHolder>() {

    inner class BusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val busNumberTextView: TextView = itemView.findViewById(R.id.busNumberTextView)
        val routeTextView: TextView = itemView.findViewById(R.id.routeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):Busadapter.BusViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_busroute, parent, false)
        return BusViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:Busadapter.BusViewHolder, position: Int) {
        val currentBus = busList[position]
        holder.busNumberTextView.text = currentBus.number
        holder.routeTextView.text = currentBus.route
    }

    override fun getItemCount() = busList.size

    fun setData(newBusList: List<Bus>) {
        busList = newBusList
        notifyDataSetChanged()
        Log.d("DataAdapter", "Data set: $newBusList")
        }

}