package com.ac.maduidesigns


import com.ac.maduidesigns.ReminderBroadcastReceiver
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class notificFragment : Fragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var buttonPickDateTime: Button
    private lateinit var buttonSetReminder: Button
    private lateinit var listViewReminders: ListView
    private lateinit var date: TextView
    private lateinit var time : TextView

    private val reminders = mutableListOf<String>() // List to store reminders
    private val reminderIds = mutableListOf<String>() // List to store reminder document IDs
    private lateinit var remindersAdapter: ArrayAdapter<String>
    private lateinit var alarmManager: AlarmManager // Initialize AlarmManager
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Variable to store selected date and time
    private var selectedDateTimeInMillis: Long = 0

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.fragment_notific, container, false)

        editTextTitle = view.findViewById(R.id.editTextTitle)
        buttonPickDateTime = view.findViewById(R.id.buttonPickDateTime)
        buttonSetReminder = view.findViewById(R.id.buttonSetReminder)
        listViewReminders = view.findViewById(R.id.listViewReminders)

        remindersAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, reminders)
        listViewReminders.adapter = remindersAdapter

        // Initialize alarmManager
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val userid = auth.currentUser?.uid
        userid?.let { loadRemindersFromFirestore() }
        // Set item click listener for the list view
        listViewReminders.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                deleteReminder(position)
            }

        buttonPickDateTime.setOnClickListener {
            // Implement date and time picker logic
            // and set the selected date and time to variables
            pickDateTime()
        }


        buttonSetReminder.setOnClickListener {
            val title = editTextTitle.text.toString()
            val date = selectedDate
            val time = selectedTime

            if (title.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                setReminder(title, date, time)
                storeReminderInFirestore(title, date, time)
                editTextTitle.setText("")
            } else {
                Toast.makeText(requireContext(), "Please select title, date, and time", Toast.LENGTH_SHORT).show()
            }
        }

        // Load reminders from Firestore


        return view
    }

    private fun pickDateTime() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        selectedDate = formatDate(calendar.time)
                        selectedTime = formatTime(calendar.time)


                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }




    @RequiresApi(Build.VERSION_CODES.S)
    private fun setReminder(title: String, date: String, time: String) {
        val reminderDateTime = "$date $time"
        val reminderDateTimeMillis = convertToMillis(reminderDateTime)

        val alarmIntent = Intent(requireContext(), ReminderBroadcastReceiver::class.java).apply {
            action = "com.ac.maduidesigns.REMINDER"
            putExtra("title", title)
            // Add any other data you want to pass to the broadcast receiver
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Add FLAG_IMMUTABLE here
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderDateTimeMillis,
                pendingIntent
            )
            Toast.makeText(requireContext(), "Reminder set for $reminderDateTime", Toast.LENGTH_SHORT).show()
        } else {
            // Handle the case where your app doesn't have permission to schedule exact alarms
            // For example, prompt the user to grant the permission or fallback to another option
            Toast.makeText(requireContext(), "App does not have permission to schedule exact alarms", Toast.LENGTH_SHORT).show()
        }
    }


    private fun convertToMillis(dateTime: String): Long {
        val format = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val date = format.parse(dateTime)
        return date?.time ?: 0
    }




    private fun storeReminderInFirestore(title: String, date: String, time: String) {
        val reminderData = hashMapOf(
            "title" to title,
            "date" to date,
            "time" to time
        )
        val userid = auth.currentUser?.uid

        if (userid != null) {
            firestore.collection("reminders").document(userid).collection("username")
                .add(reminderData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Reminder added on $date at $time", Toast.LENGTH_SHORT).show()
                    loadRemindersFromFirestore() // Reload reminders after adding a new one
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to add reminder: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadRemindersFromFirestore() {
        val userid = auth.currentUser?.uid
        if (userid != null) {
            firestore.collection("reminders").document(userid).collection("username")
                .get()
                .addOnSuccessListener { documents ->
                    reminders.clear()
                    for (document in documents) {
                        val title = document.getString("title")
                        val date = document.getString("date")
                        val time = document.getString("time")
                        if (title != null && date != null && time != null) {
                            reminders.add("$title - $date $time")
                            reminderIds.add(document.id)
                        }
                    }
                    remindersAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to retrieve reminders: $e", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun formatTime(date: Date): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(date)
    }

    private fun deleteReminder(position: Int) {
        val userid = auth.currentUser?.uid
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Confirm Deletion")
        alertDialogBuilder.setMessage("Are you sure you want to delete this reminder?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val reminderId = reminderIds[position]
            if (userid != null) {
                firestore.collection("reminders").document(userid).collection("username")
                    .document(reminderId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Reminder deleted", Toast.LENGTH_SHORT).show()
                        loadRemindersFromFirestore() // Reload reminders after deletion
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to delete reminder: $e", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog if "No" is clicked
        }
        alertDialogBuilder.show()
    }

    companion object {
        private var selectedDate: String = ""
        private var selectedTime: String = ""
    }
}