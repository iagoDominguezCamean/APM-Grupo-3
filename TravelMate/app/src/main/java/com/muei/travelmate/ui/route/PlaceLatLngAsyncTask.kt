package com.muei.travelmate.ui.route
import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PlaceLatLngAsyncTask(private val placeName: String, private val apiKey: String, private val callback: (LatLng?) -> Unit) :
    AsyncTask<Void, Void, LatLng?>() {

    override fun doInBackground(vararg params: Void?): LatLng? {
        var latLng: LatLng? = null
        try {
            // Step 1: Search for place using Places API
            val placesUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=$placeName&inputtype=textquery&fields=geometry&key=$apiKey"
            val placesJson = getJsonResponse(placesUrl)

            // Parse JSON to get latlng
            val location = JSONObject(placesJson).getJSONArray("candidates").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
            val lat = location.getDouble("lat")
            val lng = location.getDouble("lng")

            latLng = LatLng(lat, lng)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return latLng
    }

    override fun onPostExecute(result: LatLng?) {
        callback(result)
    }

    private fun getJsonResponse(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }
        reader.close()
        connection.disconnect()
        return response.toString()
    }
}
