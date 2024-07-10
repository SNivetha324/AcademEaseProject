package com.ac.maduidesigns

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class MainActivityemergencyservices : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_main_activityemergencyservices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the back button ImageView
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        // Set a click listener to handle back button click
        backButton.setOnClickListener {
            requireActivity().onBackPressed() // Navigate back when back button is clicked
        }

        // Initialize card views
        val policeCard = view.findViewById<CardView>(R.id.police)
        val fireCard = view.findViewById<CardView>(R.id.fire)

        // Handle click on the police card
        policeCard.setOnClickListener {
            val policeActivityFragment = PoliceActivity() // Assuming PoliceActivityFragment is your fragment class
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, policeActivityFragment)
            transaction.addToBackStack(null) // Optional: Adds the transaction to the back stack
            transaction.commit()
        }


        // Handle click on the fire card
        fireCard.setOnClickListener {
            val fireActivityFragment = FireActivity()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fireActivityFragment)
            transaction.addToBackStack(null) // Optional: Adds the transaction to the back stack
            transaction.commit()
        }
    }
}
