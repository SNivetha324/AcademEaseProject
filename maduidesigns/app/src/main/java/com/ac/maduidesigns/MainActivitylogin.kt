package com.ac.maduidesigns

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.View

class MainActivitylogin : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailid: EditText
    private lateinit var pass: EditText
    private lateinit var login: Button
    private lateinit var signup: TextView
    private lateinit var checkBoxShowPassword: CheckBox
    private lateinit var db: FirebaseFirestore
    private lateinit var forgot: TextView
    private val CHANNEL_ID = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activitylogin)

        createNotificationChannel()

        auth = FirebaseAuth.getInstance()

        emailid = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        login = findViewById(R.id.btnlogin)
        signup = findViewById(R.id.regis_link)
        forgot = findViewById(R.id.forgotpass)
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword)

        // Toggle password visibility
        checkBoxShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                pass.transformationMethod = null
            } else {
                pass.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            }
        }

        signup.setOnClickListener {
            val intent = Intent(this, MainActivity3signup::class.java)
            startActivity(intent)
            finish()
        }

        forgot.setOnClickListener {
            resetPassword()
        }

        if (SessionManager.isLoggedIn(this)) {
            // User is already logged in, navigate to the Navigation activity
            navigateToHomeScreen()
        }




        login.setOnClickListener {
            val email = emailid.text.toString()
            val password = pass.text.toString()
            if (checkInput()) {

                loginUser(email, password)
            } else {

                if (email.isEmpty()) {
                    emailid.error = "Please enter your username"
                    emailid.requestFocus()
                }

                if (password.isEmpty()) {
                    pass.error = "Please enter your password"
                    pass.requestFocus()

                }

                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkInput(): Boolean {
        val email = emailid.text.toString().trim()
        val password = pass.text.toString().trim()

        if (email.isEmpty()) {
            emailid.error = "Please enter your email"
            emailid.requestFocus()
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailid.error = "Invalid email format"
            emailid.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            pass.error = "Please enter your password"
            pass.requestFocus()
            return false
        } else if (password.length < 6) {
            pass.error = "Password should be at least 6 characters long"
            pass.requestFocus()
            return false
        }

        return true
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // User is signed in, navigate to the MainActivitynavigation
                        Toast.makeText(
                            baseContext, "Login successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        SessionManager.setIsLoggedIn(this, true)
                        navigateToHomeScreen()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivitynavigation::class.java)
        startActivity(intent)
        finish()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    private fun resetPassword() {
        val email = emailid.text.toString().trim()
        if (email.isEmpty()) {
            emailid.error = "Enter Email id"
            emailid.requestFocus()
        } else {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Password reset link sent please check email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(baseContext, task.exception.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }

    }
}


