package com.ac.maduidesigns

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.ac.maduidesigns.REMINDER") {
            val title = intent.getStringExtra("title")
            // You can retrieve other data passed through intent similarly
            NotificationHelper.showNotification(context, title ?: "Reminder", "It's time for $title")
        }
            // Handle the reminder event here, for example, showing a notification or triggering some action
           // Toast.makeText(context, "Reminder: $title", Toast.LENGTH_SHORT).show()
        }
    }

