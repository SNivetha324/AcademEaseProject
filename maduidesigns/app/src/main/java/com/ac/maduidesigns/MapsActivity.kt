package com.ac.maduidesigns

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.ac.maduidesigns.databinding.ActivityMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import com.google.android.gms.maps.model.Marker
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mMap: GoogleMap? = null
    private var binding: ActivityMapsBinding? = null
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null
    private var lastLocation: Location? = null
    private var currentUserLocationMarker: Marker? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private val proximityRadius = 30000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission()
        }
        var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            googleApiClient!!.disconnect()
        }
    }

    fun onClick(v: View) {
        if (v.id == R.id.search) {
            searchLocation()
        } else if (v.id == R.id.hostels_nearby) {
            searchNearbyPlaces("hostel")
        }
    }

    private fun searchLocation() {
        val addressField = findViewById<View>(R.id.location_search) as EditText
        val address = addressField.text.toString()
        var addressList: List<Address>? = null
        val userMarkerOptions = MarkerOptions()
        if (!TextUtils.isEmpty(address)) {
            val geocoder = Geocoder(this)
            try {
                addressList = geocoder.getFromLocationName(address, 10)
                if (addressList != null) {
                    for (i in addressList.indices) {
                        val userAddress = addressList[i]
                        val latLng = LatLng(userAddress.latitude, userAddress.longitude)
                        userMarkerOptions.position(latLng)
                        userMarkerOptions.title(address)
                        userMarkerOptions.icon(
                            BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_VIOLET
                            )
                        )
                        mMap!!.addMarker(userMarkerOptions)
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
                    }
                } else {
                    Toast.makeText(this, "Location Not found", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, "Please enter your location", Toast.LENGTH_LONG).show()
        }
    }

    private fun searchNearbyPlaces(placeType: String) {
        mMap?.clear()
        val url = getUrl(latitude, longitude, placeType)
        if (url.isNotEmpty()) {
            fetchNearbyPlaces(url)
            Toast.makeText(this, "Searching for Nearby $placeType...", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Invalid URL for the query", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getUrl(latitude: Double, longitude: Double, placeType: String): String {
        return if (latitude != 0.0 && longitude != 0.0 && placeType.isNotEmpty()) {
            val overpassURL = StringBuilder("https://overpass-api.de/api/interpreter?data=")
            val query = "[out:json];node(around:$proximityRadius,$latitude,$longitude)[\"tourism\"=\"$placeType\"];out;"
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            overpassURL.append(encodedQuery)
            overpassURL.toString()
        } else {
            Log.e("MapsActivity", "Invalid parameters for constructing URL")
            ""
        }
    }




    private fun fetchNearbyPlaces(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("fetchNearbyPlaces", "Response Body: $responseBody")
                parseNearbyPlaces(responseBody) // Check if the response body is not null
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("MapsActivity", "Failed to fetch nearby places: ${e.message}")
                runOnUiThread {
                    Toast.makeText(
                        this@MapsActivity,
                        "Failed to fetch nearby places",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun parseNearbyPlaces(responseBody: String?) {
        if (responseBody.isNullOrEmpty()) {
            Log.e("MapsActivity", "Empty response body")
            runOnUiThread {
                Toast.makeText(
                    this@MapsActivity,
                    "Empty response body",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        try {
            val jsonObject = JSONObject(responseBody)
            val elements = jsonObject.getJSONArray("elements")
            for (i in 0 until elements.length()) {
                val element = elements.getJSONObject(i)
                val lat = element.getDouble("lat")
                val lon = element.getDouble("lon")
                val tags = element.optJSONObject("tags")
                val name = tags?.optString("name", "Unknown") ?: "Unknown"
                val latLng = LatLng(lat, lon)

                // Ensure UI operations are performed on the main thread
                runOnUiThread {
                    try {
                        mMap?.addMarker(
                            MarkerOptions().position(latLng).title(name)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )
                    } catch (e: Exception) {
                        Log.w("MapsActivity", "Couldn't create marker: ${e.message}")
                    }
                }
            }
        } catch (e: JSONException) {
            Log.e("MapsActivity", "Error parsing nearby places JSON: ${e.message}")
            runOnUiThread {
                Toast.makeText(
                    this@MapsActivity,
                    "Error parsing nearby places",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("MapsActivity", "Error: ${e.message}")
            runOnUiThread {
                Toast.makeText(
                    this@MapsActivity,
                    "Error occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }

    fun checkUserLocationPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient()
                        }
                        mMap!!.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        locationRequest = LocationRequest.create()
        locationRequest!!.setInterval(1000)
        locationRequest!!.setFastestInterval(1000)
        locationRequest!!.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)

        // Request location updates
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient!!,
                locationRequest!!,
                this
            )
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onLocationChanged(location: Location) {
        latitude = location.latitude
        longitude = location.longitude
        lastLocation = location
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker!!.remove()
        }
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("user Current Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        currentUserLocationMarker = mMap!!.addMarker(markerOptions)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomBy(14f))
        // Check if GoogleApiClient is connected before removing location updates
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient!!, this)
        }
    }

    companion object {
        private const val REQUEST_CODE = 101
    }
}
