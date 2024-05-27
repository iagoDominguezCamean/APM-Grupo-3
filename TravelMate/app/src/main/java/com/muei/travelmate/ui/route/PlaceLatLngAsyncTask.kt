package com.muei.travelmate.ui.route
import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class PlaceLatLngAsyncTask(private val placeName: String, private val apiKey: String, private val callback: (JSONObject?) -> Unit) :
    AsyncTask<Void, Void, JSONObject?>() {

    override fun doInBackground(vararg params: Void?): JSONObject? {
        var latLngJson: JSONObject? = null
        try {
            // Step 1: Search for place using Places API
            val placesUrl = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=$placeName&inputtype=textquery&fields=geometry&key=$apiKey"
            val placesJson = getJsonResponse(placesUrl)

            // Parse JSON to get latlng
            latLngJson = JSONObject(placesJson).getJSONArray("candidates").getJSONObject(0)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return latLngJson
    }

    override fun onPostExecute(result: JSONObject?) {
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
