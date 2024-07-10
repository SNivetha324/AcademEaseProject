package com.ac.maduidesigns

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class settingsActivity : Fragment() {
    private lateinit var modeSwitchCompat: SwitchCompat
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var privateAccountSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var about: ImageView
    private lateinit var messageLayout: RelativeLayout
    private lateinit var logout: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_settings, container, false)

        modeSwitchCompat = view.findViewById(R.id.nightModeSwitch)
        notificationSwitch = view.findViewById(R.id.notificationSwitch)
        //privateAccountSwitch = view.findViewById(R.id.privateAccountSwitch)
        about = view.findViewById(R.id.aboutus)
        logout = view.findViewById(R.id.logouttxt)
        messageLayout = view.findViewById(R.id.messageLayout)

        about.setOnClickListener {
            val intent = Intent(activity, AboutUsActivity::class.java)
            startActivity(intent)
        }

        messageLayout.setOnClickListener {
            openWhatsApp()
        }

        sharedPreferences = requireContext().getSharedPreferences("Mode", Context.MODE_PRIVATE)

        val nightMode = sharedPreferences.getBoolean("night", false)
        modeSwitchCompat.isChecked = nightMode

        modeSwitchCompat.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("night", isChecked).apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            activity?.recreate()
        }

        handleNotificationSwitch()
        //handlePrivateAccountSwitch()
        logout.setOnClickListener {
            handleLogout()
        }

        return view
    }

    private fun openWhatsApp() {
        try {
            val phoneNumber = "9894679982" // Replace with the phone number you want to message
            val uri = Uri.parse("smsto:$phoneNumber")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNotificationSwitch() {
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notification_enabled", false)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("notification_enabled", isChecked)
            editor.apply()

            if (isChecked) {
                Toast.makeText(activity, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLogout() {
            val editor = context?.getSharedPreferences("session_pref", Context.MODE_PRIVATE)?.edit()
        if (editor != null) {
            editor.clear()
        }
        if (editor != null) {
            editor.apply()
        }
        // Start LoginActivity
        val intent = Intent(requireContext(), MainActivitylogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
   /* private fun handlePrivateAccountSwitch() {
        privateAccountSwitch.isChecked = sharedPreferences.getBoolean("private_account_enabled", false)

        privateAccountSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("private_account_enabled", isChecked)
            editor.apply()

            if (isChecked) {
                Toast.makeText(activity, "Private Account enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Private Account disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}
