package com.muei.travelmate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.muei.travelmate.databinding.FragmentFavBinding
import android.content.SharedPreferences
import android.util.Log
import com.muei.travelmate.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject
import kotlin.random.Random

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
    }
    override fun onStart() {
        super.onStart()
        performNetworkOperationAndFetchTracks()
    }

    private fun performNetworkOperationAndFetchTracks() {
        CoroutineScope(Dispatchers.IO).launch {
            performNetworkOperation()
            getTracks("")
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

    private suspend fun getTracks(place: String){

        for (i in 1..5){

            var s = "a"

            withContext(Dispatchers.IO) {
                // Crear petición a la API de Spotify
                val request = Request.Builder()
                    .url("https://api.spotify.com/v1/search?q=$s&type=track")
                    .get()
                    .addHeader("Authorization", "Bearer $SpotifyAccessTokenKey")
                    .build()

                try {
                    // Hacer petición
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val res = response.body?.string()

                    // Comprobar respuesta y obtener info
                    if(res != null){
                        val jsonObject = JSONObject(res)
                        val tracks = jsonObject.getJSONObject("tracks")
                        val items = tracks.getJSONArray("items")

                        for (j in 0 until items.length()){
                            val item = items.getJSONObject(j)
                            val name = item.getString("name")
                            val duration = item.getLong("duration_ms")


                            val artists = item.getJSONArray("artists")
                            val artistNames = mutableListOf<String>()
                            for (k in 0 until artists.length()) {
                                val artist = artists.getJSONObject(k)
                                artistNames.add(artist.getString("name"))
                            }
                            CoroutineScope(Dispatchers.IO).launch {
                                // Simula obtener canciones desde la API de Spotify
                                val songs = listOf(
                                    Song("Song 1", "Artist 1"),
                                    Song("Song 2", "Artist 2"),
                                    Song("Song 3", "Artist 3")
                                )
                                withContext(Dispatchers.Main) {
                                    songAdapter = SongAdapter(songs)
                                    recyclerView.adapter = songAdapter
                                }
                            }

                            Log.d("ResponseOnStart", artistNames.joinToString(", ") +": "+ name + "; duration (ms): "+duration)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}