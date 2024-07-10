package com.ac.maduidesigns

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.Adapter.eventadapter
import com.ac.maduidesigns.Model.eventsmodel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.ac.maduidesigns.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity3events : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: eventadapter
    private lateinit var eventsList: ArrayList<eventsmodel>
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main_activity3events, container, false)
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val search = view.findViewById<ImageButton>(R.id.searchevents)
        search.setOnClickListener{
            val fragment1 = Searchevents()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment1)
                .addToBackStack(null)
                .commit()
        }

        val add = view.findViewById<FloatingActionButton>(R.id.fab)
        add.setOnClickListener {
            val fragment = MainActivitynewevents() // Assuming MainActivity3newtask is a Fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment) // Replace fragment_container with the id of your fragment container in the layout
                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                .commit()
        }

        recyclerView = view.findViewById(R.id.events)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventsList = arrayListOf()
        eventsAdapter = eventadapter(eventsList)
        recyclerView.adapter = eventsAdapter

        fetchUpcomingEvents()

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchUpcomingEvents() {
        val currentDate = getCurrentDateAsString()
        Log.d("Dateformat", "$currentDate")
        db.collection("events")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document in querySnapshot.documents) {
                    val event = document.toObject(eventsmodel::class.java)
                    event?.let {
                        val eventDate = it.date // Assuming the date is stored as a String in the format "dd/MM/yyyy"
                        val eventLocalDate = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        if (!eventLocalDate.isBefore(LocalDate.parse(currentDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")))) {
                            eventsList.add(it)
                        }
                    }
                }
                eventsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateAsString(): String {
        // Get the current system date
        val currentDate = LocalDate.now()

        // Define the date format
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Customize the format as needed

        // Format the date to string
        return currentDate.format(formatter)
    }



}