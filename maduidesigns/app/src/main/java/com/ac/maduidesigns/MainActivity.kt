package com.ac.maduidesigns


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Thread.sleep(2000)
//        installSplashScreen()
        setContentView(R.layout.activity_main)

        val hostel = findViewById<CardView>(R.id.hostel)
        hostel.setOnClickListener(View.OnClickListener {
            val intent1 = Intent(this, MainActivity3hostel::class.java)
            startActivity(intent1)
        })

        val emergency = findViewById<CardView>(R.id.emrser)
        emergency.setOnClickListener(View.OnClickListener {
            val intent2 = Intent(this, MainActivityemergencyservices::class.java)
            startActivity(intent2)
        })

        val transport = findViewById<CardView>(R.id.trans)
        transport.setOnClickListener(View.OnClickListener {
            val intent3 = Intent(this, MainActivity3pubtrans::class.java)
            startActivity(intent3)
        })

        val events = findViewById<CardView>(R.id.events)
        events.setOnClickListener(View.OnClickListener {
            val intent4 = Intent(this, MainActivity3events::class.java)
            startActivity(intent4)
        })

        val plan = findViewById<CardView>(R.id.plans)
        plan.setOnClickListener(View.OnClickListener {
            val intent5 = Intent(this, MainActivity2::class.java)
            startActivity(intent5)
        })

        /*val settings = findViewById<CardView>(R.id.settings)
        settings.setOnClickListener(View.OnClickListener {
            val intent5 = Intent(this, MainActivity2::class.java)
            startActivity(intent5)
        })*/
    }
}
