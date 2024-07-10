package com.ac.maduidesigns

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class dataParser {
    fun parse(jsonData: String?): List<HashMap<String, String>?> {
        val nearbyPlacesList: MutableList<HashMap<String, String>?> = ArrayList()

        try {
            val jsonObject = JSONObject(jsonData)
            val elements = jsonObject.getJSONArray("elements")

            for (i in 0 until elements.length()) {
                val element = elements.getJSONObject(i)
                val name = element.getString("tags")  // Adjust the field name according to OpenStreetMap API response
                val lat = element.getDouble("lat")
                val lon = element.getDouble("lon")

                val placeMap = HashMap<String, String>()
                placeMap["place_name"] = name
                placeMap["lat"] = lat.toString()
                placeMap["lon"] = lon.toString()

                nearbyPlacesList.add(placeMap)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return nearbyPlacesList
    }
}
