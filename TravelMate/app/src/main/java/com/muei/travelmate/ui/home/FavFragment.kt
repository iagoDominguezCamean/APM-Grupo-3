package com.muei.travelmate.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.muei.travelmate.databinding.FragmentFavBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.net.Uri
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.Connector
import com.spotify.protocol.types.Track
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
//import com.spotify.sdk.android.auth.AuthorizationRequest
//import com.spotify.sdk.android.auth.AuthorizationResponse
import com.spotify.android.appremote.api.ConnectionParams

import com.adamratzman.spotify.GenericSpotifyApi
import com.adamratzman.spotify.SpotifyApi
import com.adamratzman.spotify.SpotifyApiOptions
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyUserAuthorization
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.refreshSpotifyClientToken
import com.adamratzman.spotify.spotifyClientPkceApi
import com.adamratzman.spotify.spotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyScope


class FavFragment : Fragment() {

    private val client_Id = "34e03110e5914b75aafadef83af8e9d1"
    private val cliente_secret= "0ce85421871748b898eae53b4b5c15d3"
    private val redirect_Uri = "http://travelmate/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    //private var spotifyAppRemote: com.spotify.android.appremote.api.SpotifyAppRemote? = null
    private var _binding: FragmentFavBinding? = null
    private val binding get() = _binding!!

    public val SpotifyTokenExpiryKey: String = "spotifyTokenExpiry"
    public val SpotifyAccessTokenKey: String = "spotifyAccessToken"
    public val SpotifyRefreshTokenKey: String = "spotifyRefreshToken"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val redirect_Uri = "http://travelmate/callback"

        val connectionParams = ConnectionParams.Builder(client_Id)
            .setRedirectUri(redirect_Uri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(requireContext(), connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                    spotifyAppRemote = appRemote
                    Log.d("FavFragment", "Connected! Yay!")
                    connected()
                }

            override fun onFailure(throwable: Throwable) {
                    Log.e("FavFragment", throwable.message, throwable)
                }
            })
    }

    fun handleRedirection(data: Uri) {
        // Aquí puedes manejar el URI recibido y hacer lo necesario
        Log.d("FavFragment", "Redireccionamiento recibido: $data")
    }

    private fun connected() {
        spotifyAppRemote?.let {
            // Reproducir una lista de reproducción
            val playlistURI = "https://open.spotify.com/intl-es/album/2FhnW6RWMy7EV4rOUphZGT?si=YUbA9GKaTUC1H68iSKEuVQ"
            it.playerApi.play(playlistURI)

            // Suscribirse al estado del reproductor
            it.playerApi.subscribeToPlayerState().setEventCallback { playerState ->
                val track = playerState.track
                track?.let {
                    Log.d("FavFragment", "${track.name} by ${track.artist.name}")
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
        _binding = null
    }
}
