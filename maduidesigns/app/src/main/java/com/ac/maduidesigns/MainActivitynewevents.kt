package com.ac.maduidesigns

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivitynewevents : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var addButton: FloatingActionButton
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var venueSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var db: FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main_activitynewevents, container, false)

        // Initialize other views
        titleEditText = view.findViewById(R.id.edt1)
        descriptionEditText = view.findViewById(R.id.eventDescriptionEditText)
        datePicker = view.findViewById(R.id.datepicker)
        timePicker = view.findViewById(R.id.timepicker)
        venueSpinner = view.findViewById(R.id.venuespin)

        val calendar = Calendar.getInstance()
// Set minimum date to today's date
        datePicker.minDate = calendar.timeInMillis


        // Initialize spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.venue_names,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            venueSpinner.adapter = adapter
        }

        citySpinner = view.findViewById(R.id.citySpinner)

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        backButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        addButton = view.findViewById(R.id.tick_button)
        addButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val date = formatDate(datePicker.dayOfMonth, datePicker.month+1, datePicker.year)
            val time = formatTime(timePicker.hour, timePicker.minute)
            val venue = venueSpinner.selectedItem.toString()
            val city = citySpinner.selectedItem.toString()
            val year = datePicker.year
            val month = datePicker.month
            val dayOfMonth = datePicker.dayOfMonth

            val calendardate = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateintimestamp = calendardate.time
            // Check for overlapping events
            checkForOverlappingEvents(title, description, venue, date, time, city,dateintimestamp)
        }

        return view
    }

    private fun formatDate(day: Int, month: Int, year: Int): String {
        return "$day/${month.toString().padStart(2, '0')}/$year"
    }



    private fun formatTime(hour: Int, minute: Int): String {
        val hourFormatted = when {
            hour == 0 -> "12:${minute.toString().padStart(2, '0')} AM"
            hour < 12 -> "$hour:${minute.toString().padStart(2, '0')} AM"
            hour == 12 -> "12:${minute.toString().padStart(2, '0')} PM"
            else -> "${hour - 12}:${minute.toString().padStart(2, '0')} PM"
        }
        return hourFormatted
    }

    private fun checkForOverlappingEvents(
        title: String,
        description: String,
        venue: String,
        date: String,
        time: String,
        city: String,
        tsdate: Date
    ) {
        db.collection("events")
            .whereEqualTo("venue", venue)
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .whereEqualTo("city", city)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(
                        requireContext(),
                        "Event overlaps with existing event, please select a different date, time, or venue",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Add event to Firestore
                    val event = hashMapOf(
                        "title" to title,
                        "description" to description,
                        "date" to date,
                        "time" to time,
                        "venue" to venue,
                        "city" to city,
                        "tsdate" to tsdate
                    )
                    db.collection("events")
                        .add(event)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                requireContext(),
                                "Event added successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Error adding event: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Error checking for overlapping events: $exception",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}