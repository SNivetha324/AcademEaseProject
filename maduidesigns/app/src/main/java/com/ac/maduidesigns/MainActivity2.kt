package com.ac.maduidesigns

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.Adapter.todoadapter
import com.ac.maduidesigns.EditTasksActivity
import com.ac.maduidesigns.Model.todomodel
import com.ac.maduidesigns.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity2 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: todoadapter
    private lateinit var taskList: ArrayList<todomodel>
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main2, container, false)

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val add = view.findViewById<FloatingActionButton>(R.id.fab)
        add.setOnClickListener {
            val fragment = MainActivity3newtask() // Assuming MainActivity3newtask is a Fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment) // Replace fragment_container with the id of your fragment container in the layout
                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                .commit()
        }

        recyclerView = view.findViewById(R.id.mytask)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        taskList = arrayListOf()
        taskAdapter = todoadapter(taskList) { task ->
            onItemClick(task)
        }
        recyclerView.adapter = taskAdapter

        auth = FirebaseAuth.getInstance()
        val userid = auth.currentUser?.uid

        userid?.let { fetchTasks(it) }

        return view
    }

    private fun fetchTasks(userid: String) {
        db.collection("mytasks")
            .document(userid)
            .collection("username")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                taskList.clear() // Clear the existing list before adding new tasks
                for (document in querySnapshot.documents) {
                    val mytask: todomodel? = document.toObject(todomodel::class.java)
                    mytask?.let { taskList.add(it) }
                }
                taskAdapter.notifyDataSetChanged() // Notify adapter that the data set has changed
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching tasks: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onItemClick(item: todomodel) {
        val options = arrayOf("Edit", "Delete")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose an option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editTask(item)
                    1 -> deleteTask(item)
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun editTask(item: todomodel) {
        // Launch the EditTasksActivity fragment with the task ID
        val fragment = EditTasksActivity() // Assuming EditTasksFragment is your fragment
        val bundle = Bundle()
        bundle.putString("taskId", item.id)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment) // Replace frame_container with the ID of your fragment container
            .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
            .commit()
    }


    private fun deleteTask(item: todomodel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Delete") { _, _ ->
                // Delete from Firestore
                deleteTaskFromFirestore(item)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteTaskFromFirestore(item: todomodel) {
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        val taskId = item.id

        if (userId != null && taskId != null) {
            db.collection("mytasks")
                .document(userId)
                .collection("username")
                .document()
                .delete()
                .addOnSuccessListener {
                    // Remove the item from the local list and update RecyclerView
                    taskList.remove(item)
                    taskAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error deleting task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
