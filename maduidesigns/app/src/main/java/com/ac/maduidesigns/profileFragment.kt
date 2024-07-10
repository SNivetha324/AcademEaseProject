package com.ac.maduidesigns

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class profileFragment : Fragment() {

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var cityTextView: TextView
    private lateinit var editProfileButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        usernameTextView = view.findViewById(R.id.usernameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        phoneTextView = view.findViewById(R.id.phoneTextView)
        genderTextView = view.findViewById(R.id.genderTextView)
        cityTextView = view.findViewById(R.id.cityTextView)
        editProfileButton = view.findViewById(R.id.editprofile)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Fetch user details from Firestore
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email != null) {
            // Fetching user details from "USERS" collection
            db.collection("USERS").document(email).get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument != null) {
                        usernameTextView.text = userDocument.getString("Name")
                        emailTextView.text = userDocument.getString("email")
                        phoneTextView.text = userDocument.getString("Phone")
                        // Fetching gender and city from the "USERS" collection itself
                        val gender = userDocument.getString("Gender")
                        val city = userDocument.getString("City")
                        genderTextView.text = gender
                        cityTextView.text = city
                    } else {
                        Log.d("ProfileFragment", "No user document found")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Error getting user data", exception)
                }
        }

        editProfileButton.setOnClickListener {
            // Navigate to EditProfileActivity
            val intent = Intent(activity, EditProfileActivity::class.java)
            // Pass user details as extras to the intent
            intent.putExtra("name", usernameTextView.text.toString())
            intent.putExtra("email", emailTextView.text.toString())
            intent.putExtra("phone", phoneTextView.text.toString())

            // Pass gender and city if available
            intent.putExtra("gender", genderTextView.text.toString())
            intent.putExtra("city", cityTextView.text.toString())

            startActivity(intent)
        }

        return view
    }

}
