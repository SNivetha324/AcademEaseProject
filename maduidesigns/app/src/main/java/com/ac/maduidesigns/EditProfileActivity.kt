package com.ac.maduidesigns

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.SetOptions

class EditProfileActivity : AppCompatActivity() {

    private lateinit var editUsernameEditText: EditText
    private lateinit var editEmailEditText: EditText
    private lateinit var editPhoneEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        editUsernameEditText = findViewById(R.id.usernameEditText)
        editEmailEditText = findViewById(R.id.emailEditText)
        editPhoneEditText = findViewById(R.id.phoneEditText)
        genderEditText = findViewById(R.id.genderEditText)
        cityEditText = findViewById(R.id.cityEditText)
        saveButton = findViewById(R.id.saveButton)

        // Retrieve user details passed from profileFragment
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("phone")
        val gender = intent.getStringExtra("gender")
        val city = intent.getStringExtra("city")
        // Set the retrieved user details to the EditText fields
        editUsernameEditText.setText(name)
        editEmailEditText.setText(email)
        editEmailEditText.isFocusable = false // Make email EditText non-editable
        editEmailEditText.isFocusableInTouchMode = false
        editPhoneEditText.setText(phone)
        genderEditText.setText(gender)
        cityEditText.setText(city)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        saveButton.setOnClickListener {
            // Retrieve the current user's email
            val currentUserEmail = auth.currentUser?.email

            // Update the document ID to the new email address
            val newEmail = editEmailEditText.text.toString()

            // Create a new map to store the updated user data
            val userMap = hashMapOf(
                "Name" to editUsernameEditText.text.toString(),
                "email" to newEmail,
                "Phone" to editPhoneEditText.text.toString(),
                "Gender" to genderEditText.text.toString(),
                "City" to cityEditText.text.toString()
            )

            // Check if the user is changing their email address
            if (currentUserEmail != newEmail) {
                // Create a new document with the updated email address
                val newDocRef = firestore.collection("USERS").document(newEmail)
                // Move the data to the new document
                val currentUserDoc = firestore.collection("USERS").document(currentUserEmail!!)
                currentUserDoc.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        newDocRef.set(documentSnapshot.data!!)
                        // Delete the old document
                        currentUserDoc.delete()
                    }
                }.addOnFailureListener { exception ->
                    // Handle failure
                    Toast.makeText(this, "Error updating email: $exception", Toast.LENGTH_SHORT).show()
                }
            }

            // Update or create the document in the USERS collection
            val userId = auth.currentUser?.uid
            userId?.let {
                firestore.collection("USERS").document(newEmail)
                    .set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Details saved", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this,"Error saving details", Toast.LENGTH_LONG).show()
                    }
            }
        }


    }
}
