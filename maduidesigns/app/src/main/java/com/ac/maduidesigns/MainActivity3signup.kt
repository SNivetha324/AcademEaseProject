package com.ac.maduidesigns


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity3signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var uname: EditText
    private lateinit var confirm_pass: EditText
    private lateinit var phone: EditText
    private lateinit var emailid: EditText
    private lateinit var password: EditText
    private lateinit var register: Button
    private lateinit var signin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainsignup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        uname = findViewById(R.id.usrname)
        phone = findViewById(R.id.phno)
        emailid = findViewById(R.id.emailid)
        password = findViewById(R.id.password)
        confirm_pass = findViewById(R.id.confpass)
        register = findViewById(R.id.btnsignup)
        signin = findViewById(R.id.regis_link)

        signin.setOnClickListener {
            val intent = Intent(this, MainActivitylogin::class.java)
            startActivity(intent)
        }

        register.setOnClickListener {
            if (validateInputs()) {
                val email = emailid.text.toString()
                val password = password.text.toString()
                val name = uname.text.toString()
                val phone = phone.text.toString()

                val user = hashMapOf(
                    "Name" to name,
                    "Phone" to phone,
                    "email" to email
                )

                val usersRef = db.collection("USERS")
                usersRef.whereEqualTo("email", email).get().addOnSuccessListener { tasks ->
                    if (tasks.isEmpty) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registered successfully",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    usersRef.document(email).set(user)
                                    val intent = Intent(
                                        this,
                                        MainActivitynavigation::class.java
                                    )
                                    intent.putExtra("email", email)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Authentication Failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "User already registered",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this, MainActivitylogin::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = uname.text.toString().trim()
        val email = emailid.text.toString().trim()
        val phone = phone.text.toString().trim()
        val password = password.text.toString()
        val confirmPass = confirm_pass.text.toString()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPass) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
