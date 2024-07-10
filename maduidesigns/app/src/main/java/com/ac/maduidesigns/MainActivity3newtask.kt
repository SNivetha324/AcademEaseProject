package com.ac.maduidesigns

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MainActivity3newtask : Fragment() {

    // Constants for notification
    private val channelID = "Task_Reminder_Channel"
    private val titleExtra = "taskTitle"
    private val messageExtra = "taskMessage"

    private lateinit var db: FirebaseFirestore
    private lateinit var task: EditText
    private lateinit var date: DatePicker
    private lateinit var time: TimePicker
    private lateinit var repeat: Spinner
    private lateinit var save: FloatingActionButton
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_main_activity3newtask, container, false)

        db = FirebaseFirestore.getInstance()
        task = view.findViewById(R.id.edt1)
        date = view.findViewById(R.id.date)
        time = view.findViewById(R.id.time)
        repeat = view.findViewById(R.id.repspin)
        save = view.findViewById(R.id.tick_button)

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val calendar = Calendar.getInstance()

        // Set minimum date to today's date
        date.minDate = calendar.timeInMillis

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_items1,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            repeat.adapter = adapter

            save.setOnClickListener {
                if (checking()) {
                    val taskName = task.text.toString()
                    val dueDate = formatDate(date.dayOfMonth, date.month + 1, date.year)
                    val dueTime = formatTime(time.hour, time.minute)
                    val repeatValue = repeat.selectedItem.toString()

                    auth = FirebaseAuth.getInstance()
                    val userId = auth.currentUser?.uid

                    userId?.let {
                        val plans = hashMapOf(
                            "name" to taskName,
                            "dueDate" to dueDate,
                            "dueTime" to dueTime,
                            "repeat" to repeatValue
                        )

                        val todoplans = db.collection("mytasks").document(userId).collection("username").document()
                        todoplans.set(plans)
                            .addOnSuccessListener { documentReference ->
                                Log.d("inserting doc", "inserted")

                                Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT)
                                    .show()

                                // Schedule notification after task is added
                                scheduleNotification(taskName, time.hour, time.minute, date.dayOfMonth, date.month, date.year)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to add task: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                } else {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun checking(): Boolean {
        val taskName = task.text.toString()
        val dueDate = "${date.dayOfMonth}/${date.month + 1}/${date.year}"
        val dueTime = "${time.hour}:${time.minute}"
        val repeatValue = repeat.selectedItem.toString()

        return taskName.isNotEmpty() && repeatValue.isNotEmpty() && dueDate.isNotEmpty() && dueTime.isNotEmpty()
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

    private fun scheduleNotification(taskName: String, hour: Int, minute: Int, day: Int, month: Int, year: Int) {
        // Create calendar instances for notification time and current time
        val notificationTime = Calendar.getInstance().apply {
            set(year, month, day, hour, minute)
            add(Calendar.HOUR_OF_DAY, -2) // Subtract 2 hours
        }
        val currentTime = Calendar.getInstance()
        if (currentTime.before(notificationTime)) {
            // If the current time is before the notification time, schedule the notification
            // Calculate the time difference between current time and notification time
            val timeDifferenceMillis = notificationTime.timeInMillis - currentTime.timeInMillis

            // Schedule a timer to trigger the notification after the time difference
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    // Send the notification when the timer triggers
                    sendNotification(taskName)
                }
            }, timeDifferenceMillis)
        } else {
            // If the current time is after the notification time, send the notification immediately
            sendNotification(taskName)
        }
    }

    private fun sendNotification(message: String) {
        val notificationId = 1
        val builder = NotificationCompat.Builder(requireContext(), channelID)
            .setSmallIcon(R.drawable.splash) // Set your notification icon
            .setContentTitle(message) // Set task name as notification title
            .setContentText("Don't forget to complete the task!") // Reminder message
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Show the notification
        with(NotificationManagerCompat.from(requireContext())) {
           if (context?.let {
                   ActivityCompat.checkSelfPermission(
                       it,
                       Manifest.permission.POST_NOTIFICATIONS
                   )
               } != PackageManager.PERMISSION_GRANTED
           ) {
               // TODO: Consider calling
               //    ActivityCompat#requestPermissions
               // here to request the missing permissions, and then overriding
               //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
               //                                          int[] grantResults)
               // to handle the case where the user grants the permission. See the documentation
               // for ActivityCompat#requestPermissions for more details.
               return
           }
            notify(notificationId, builder.build())
        }
    }
    companion object{
        private const val TAG = "TaskFragment"
        private const val CHANNEL_ID = "100"
    }
}
