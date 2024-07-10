package com.ac.maduidesigns

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class PoliceActivity : Fragment() {

    private lateinit var citySpinner: Spinner
    private lateinit var listView: ListView
    private lateinit var phoneNumber: String
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val contactsList = mutableListOf<String>()
    private val CALL_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_police, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        citySpinner = view.findViewById(R.id.citySpinner)
        listView = view.findViewById(R.id.policeListView)
        arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, contactsList)
        listView.adapter = arrayAdapter

        // Initialize the back button ImageView
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        // Set a click listener to handle back button click
        backButton.setOnClickListener {
            requireActivity().onBackPressed() // Navigate back when back button is clicked
        }

        // Populate spinner with city names
        val cities = resources.getStringArray(R.array.cities)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter

        // Set up a listener for the spinner
        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View,
                position: Int,
                id: Long
            ) {
                val selectedCity = cities[position]
                if (selectedCity != "Select City") {
                    fetchPoliceContacts(selectedCity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Set up a listener for list item clicks
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val contactInfo = contactsList[position]
            val phoneNumber = contactInfo.split(": ")[1]
            makePhoneCall(phoneNumber)
        }
    }

    private fun fetchPoliceContacts(city: String) {
        val db = FirebaseFirestore.getInstance()
        val policeContactRef = db.collection("emergencyservices").document(city)
        policeContactRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    if (data != null) {
                        contactsList.clear()
                        for (entry in data) {
                            val name = entry.key
                            val number = (entry.value as Map<*, *>)["number"].toString()
                            contactsList.add("$name: $number")
                        }
                        arrayAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "No such document!", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error fetching data: $e", Toast.LENGTH_LONG).show()
            }
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), CALL_PERMISSION_REQUEST_CODE)
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val contactInfo = contactsList[listView.checkedItemPosition]
                val phoneNumber = contactInfo.split(": ")[1]
                makePhoneCall(phoneNumber)
            } else {
                Toast.makeText(requireContext(), "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
