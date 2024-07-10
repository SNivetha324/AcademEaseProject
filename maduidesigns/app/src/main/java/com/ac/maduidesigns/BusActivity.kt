package com.ac.maduidesigns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ac.maduidesigns.Adapter.Busadapter
import com.ac.maduidesigns.Model.Bus
import com.google.firebase.firestore.FirebaseFirestore

class BusActivity : Fragment() {
    private lateinit var backButton: ImageView
    private lateinit var searchInput: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Busadapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_bus, container, false)

        backButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        searchInput = view.findViewById(R.id.searchinput)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = Busadapter(emptyList()) // Initially empty list
        recyclerView.adapter = adapter

        // Populate spinner with city names
        val cities = resources.getStringArray(R.array.cities)
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cities)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        searchInput.adapter = arrayAdapter

        // Set up item selection listener for spinner
        searchInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCity = cities[position]
                if (selectedCity != "Select City") {
                    // Fetch bus routes based on the selected city
                    fetchDataFromFirestore(selectedCity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        return view
    }

    private fun fetchDataFromFirestore(selectedCity: String) {
        val busList = mutableListOf<Bus>()

        // Reference to the Firestore collection
        val routesCollection = db.collection("busRoutes").document(selectedCity)

        // Fetch data from Firestore
        routesCollection.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    if (data != null) {
                        // Iterate over each bus number map in the Firestore document
                        for ((busNumber, routeData) in data) {
                            if (routeData is Map<*, *>) {
                                // Extract "From" and "To" fields from the route data map
                                val from = routeData["From"] as? String
                                val to = routeData["To"] as? String
                                if (from != null && to != null) {
                                    // Add the fetched route to the busList
                                    busList.add(Bus(busNumber as String, "$from-$to"))
                                }
                            }
                        }
                        // Update the RecyclerView adapter with the fetched data
                        adapter.setData(busList)
                    } else {
                        // Handle the case where data is null
                    }
                } else {
                    // Handle the case where the document doesn't exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while fetching data
            }
    }
}
