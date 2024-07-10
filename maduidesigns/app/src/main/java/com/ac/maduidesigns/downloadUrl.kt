package com.ac.maduidesigns

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class downloadUrl {
    @Throws(IOException::class)
    fun ReadUrl(placeurl: String?): String {
        var data = ""
        var inputStream: InputStream? = null
        var httpURLConnection: HttpURLConnection? = null
        try {
            val url = URL(placeurl)
            httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connect()
            inputStream = httpURLConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuffer = StringBuffer()
            var line: String? = ""
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuffer.append(line)
            }
            data = stringBuffer.toString()
            bufferedReader.close()
        } catch (e: MalformedURLException) {
            Log.e("GetNearbyPlaces", "IOException: ${e.message}")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e("GetNearbyPlaces", "IOException: ${e.message}")
            e.printStackTrace()
        } finally {
            inputStream?.close()
            httpURLConnection?.disconnect()
        }
        return data
    }
}
