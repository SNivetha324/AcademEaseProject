package com.ac.maduidesigns

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.Adapter.eventadapter
import com.ac.maduidesigns.Model.eventsmodel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import androidx.appcompat.widget.SearchView
import java.util.*

class Searchevents : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: eventadapter
    private lateinit var eventsList: ArrayList<eventsmodel>
    private lateinit var applyButton: Button
    private lateinit var datePicker: DatePicker
    private lateinit var citySpinner: Spinner
    private lateinit var noEventsImageView: ImageView
    private lateinit var search: SearchView
    private var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_searchevents, container, false)

        backButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView)
        datePicker = view.findViewById(R.id.datePicker)
        citySpinner = view.findViewById(R.id.citySpinner)
        applyButton = view.findViewById(R.id.applyButton)
        noEventsImageView = view.findViewById(R.id.noEventsImageView)


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tamil_nadu_cities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            citySpinner.adapter = adapter
        }

        db = FirebaseFirestore.getInstance()


        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        eventsList = arrayListOf()
        eventsAdapter = eventadapter(eventsList)
        recyclerView.adapter = eventsAdapter

        applyButton.setOnClickListener {
            applyFilters()
        }
        search = view.findViewById(R.id.search)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchEvents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can add some debounce logic here if needed
                return false
            }
        })

        return view
    }





    private fun applyFilters() {
        val selectedCity = citySpinner.selectedItem.toString()

        // Filter events based on selected city and date
        filterEvents(selectedCity)
    }



    private fun filterEvents(city: String) {

        db.collection("events")
            .whereEqualTo("city", city)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("Firestore", "Query successful. Found ${querySnapshot.size()} documents.")
                val events = mutableListOf<eventsmodel>()
                for (document in querySnapshot.documents) {
                    val event = document.toObject(eventsmodel::class.java)
                    event?.let { events.add(it) }
                }

                // Update visibility of ImageView based on whether events are found
                if (events.isEmpty()) {
                    noEventsImageView.visibility = View.VISIBLE
                } else {
                    noEventsImageView.visibility = View.GONE
                }
                eventsList.clear()
                eventsList.addAll(events)
                eventsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching events: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Firestore", "Error fetching events", exception)
            }
    }


    private fun searchEvents(query: String) {
        // Convert the query to lowercase for case-insensitive search
        val lowercaseQuery = query.toLowerCase(Locale.getDefault())

        // Perform a Firestore query to fetch events matching the search query
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                eventsList.clear()
                for (document in result) {
                    val event = document.toObject(eventsmodel::class.java)

                    // Convert the event name to lowercase for case-insensitive comparison
                    val lowercaseEventName = event.title.toLowerCase(Locale.getDefault())

                    // Check if the lowercase event name contains the lowercase query
                    if (lowercaseEventName.contains(lowercaseQuery)) {
                        eventsList.add(event)
                    }
                }
                eventsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to search events: $exception", Toast.LENGTH_SHORT).show()
            }
    }


}
