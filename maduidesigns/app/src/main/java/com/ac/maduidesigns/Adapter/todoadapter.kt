package com.ac.maduidesigns.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.EditTasksActivity
import com.ac.maduidesigns.Model.todomodel
import com.ac.maduidesigns.R

class todoadapter(
    private val taskList: ArrayList<todomodel>,
    private val onItemClick: (todomodel) -> Unit
) : RecyclerView.Adapter<todoadapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvtask: TextView = itemView.findViewById(R.id.taskName)
        val tvdate: TextView = itemView.findViewById(R.id.dueDate)
        val tvtime: TextView = itemView.findViewById(R.id.dueTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentItem = taskList[position]
        holder.tvtask.text = currentItem.name
        holder.tvdate.text = currentItem.dueDate
        holder.tvtime.text = currentItem.dueTime

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}
