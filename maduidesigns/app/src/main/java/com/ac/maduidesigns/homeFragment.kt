package com.ac.maduidesigns

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class homeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val nearbyhostels = view.findViewById<CardView>(R.id.hostel)
        nearbyhostels.setOnClickListener{
            val intent = Intent(requireContext(),MapsActivity::class.java)
            startActivity(intent)
//            val fragment = MapsActivity()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.frame_container, fragment) // Replace fragment_container with the id of your fragment container in the layout
//                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
//                .commit()
        }

//        val hostel = view.findViewById<CardView>(R.id.hostel)
//        hostel.setOnClickListener {
//            val intent1 = Intent(requireContext(), MainActivity3hostel::class.java)
//            startActivity(intent1)
//        }

        val emergency = view.findViewById<CardView>(R.id.emrser)
        emergency.setOnClickListener {
            val fragment2 = MainActivityemergencyservices()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment2) // Replace fragment_container with the id of your fragment container in the layout
                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                .commit()
        }

        val transport = view.findViewById<CardView>(R.id.trans)
        transport.setOnClickListener {
            val fragment3 = BusActivity()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment3) // Replace fragment_container with the id of your fragment container in the layout
                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                .commit()
        }

        val events = view.findViewById<CardView>(R.id.events)
        events.setOnClickListener {
            val fragment4 = MainActivity3events()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment4) // Replace fragment_container with the id of your fragment container in the layout
                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                .commit()
        }

        val plan = view.findViewById<CardView>(R.id.plans)
        plan.setOnClickListener {
            val fragment5 = MainActivity2() // Assuming MainActivity2 is a Fragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment5) // Replace fragment_container with the id of your fragment container in the layout
                .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                .commit()
        }


         val settings = view.findViewById<CardView>(R.id.settings)
         settings.setOnClickListener {
             val fragment6 = settingsActivity() // Assuming MainActivity2 is a Fragment
             parentFragmentManager.beginTransaction()
                 .replace(R.id.frame_container, fragment6) // Replace fragment_container with the id of your fragment container in the layout
                 .addToBackStack(null) // Optional: Add to back stack if you want to navigate back to the previous fragment
                 .commit()
         }



        return view

    }


}
