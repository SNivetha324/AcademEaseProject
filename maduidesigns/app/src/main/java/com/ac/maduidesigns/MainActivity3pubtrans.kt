package com.ac.maduidesigns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView


class MainActivity3pubtrans : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpubtrans)

        // Initialize the back button ImageView
        val backButton = findViewById<ImageView>(R.id.back_button)

        // Set a click listener to handle back button click
        backButton.setOnClickListener {
            onBackPressed() // Navigate back when back button is clicked
        }



    }

    }
