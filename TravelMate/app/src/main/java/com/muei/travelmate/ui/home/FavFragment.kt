package com.muei.travelmate.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentFavBinding
import com.muei.travelmate.ui.Music.TrackAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

class FavFragment : Fragment() {

    private val client = OkHttpClient()
    private lateinit var  client_Id: String
    private lateinit var cliente_secret :String
    private val redirect_Uri = "https://accounts.spotify.com/api/token"
    private var _binding: FragmentFavBinding? = null
    private val binding get() = _binding!!

    private var SpotifyTokenExpiryKey: String = ""
    private var SpotifyAccessTokenKey: String = ""
    private var SpotifyRefreshTokenKey: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(UserFavModel::class.java)

        client_Id = getString(R.string.spotifyClientId)
        cliente_secret = getString(R.string.spotifyClientSecret)

        _binding = FragmentFavBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        performNetworkOperationAndFetchTracks()
    }
    override fun onStart() {
        super.onStart()
        performNetworkOperationAndFetchTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun performNetworkOperationAndFetchTracks() {
        CoroutineScope(Dispatchers.IO).launch {
            performNetworkOperation()
            getTracks()?.let { tracks ->
                withContext(Dispatchers.Main){
                    initRecycleView(tracks)
                }
            }
        }
    }

    private suspend fun performNetworkOperation() {
        withContext(Dispatchers.IO) {
            val formBody = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", client_Id)
                .add("client_secret", cliente_secret)
                .build()

            val request = Request.Builder()
                .url(redirect_Uri)
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val res = response.body?.string()
                Log.d("ResponseOnStart", res.toString())

                if (res != null) {
                    val jsonObject = JSONObject(res)
                    SpotifyAccessTokenKey = jsonObject.getString("access_token")
                    SpotifyRefreshTokenKey = jsonObject.getString("access_token")
                    SpotifyTokenExpiryKey = jsonObject.getString("expires_in")

                    // You can now use these variables in your app
                    Log.d("AccessToken", SpotifyAccessTokenKey)
                    Log.d("RefreshToken", SpotifyRefreshTokenKey)
                    Log.d("TokenExpiry", SpotifyTokenExpiryKey)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getTracks(): List<List<String>>? {
        val tracks = mutableListOf<List<String>>()

        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.spotify.com/v1/search?q=a&type=track")
                .get()
                .addHeader("Authorization", "Bearer $SpotifyAccessTokenKey")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val res = response.body?.string()

                if (res != null) {
                    val jsonObject = JSONObject(res)
                    val items = jsonObject.getJSONObject("tracks").getJSONArray("items")

                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val name = item.getString("name")
                        val artists = item.getJSONArray("artists")
                        val artistNames = (0 until artists.length()).joinToString(", ") {
                            artists.getJSONObject(it).getString("name")
                        }

                        val album = item.getJSONObject("album")
                        val images = album.getJSONArray("images")
                        val imageURL = images.getJSONObject(1).getString("url")


                        tracks.add(listOf(name, artistNames,imageURL))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return tracks
    }

    fun initRecycleView(tracks: List<List<String>>){
        val recyclerView: RecyclerView = binding.root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = TrackAdapter(tracks)
    }


}