package com.ac.maduidesigns

import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class GetNearbyPlaces : AsyncTask<Any?, String?, String>() {
    private var googleplaceData: String? = null
    private var url: String? = null
    private var mMap: GoogleMap? = null

    override fun doInBackground(vararg params: Any?): String? {
        mMap = params[0] as GoogleMap
        url = params[1] as String
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url!!)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body!!.string()
        }
    }

    override fun onPostExecute(s: String) {
        if (s.isNotEmpty()) {
            val dataParser = dataParser()
            val nearbyPlacesList = dataParser.parse(s)
            displayNearbyPlaces(nearbyPlacesList)
        } else {
            Log.e("GetNearbyPlaces", "Empty response received")
        }
    }

    private fun displayNearbyPlaces(nearbyPlacesList: List<HashMap<String, String>?>) {
        for (place in nearbyPlacesList) {
            val name = place?.get("place_name")
            val lat = place?.get("lat")?.toDouble()
            val lon = place?.get("lon")?.toDouble()

            if (lat != null && lon != null) {
                val latLng = LatLng(lat, lon)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title(name)
                mMap?.addMarker(markerOptions)
            }
        }
    }
}
