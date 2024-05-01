package com.muei.travelmate.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.muei.travelmate.R
import com.muei.travelmate.databinding.FragmentUserBinding
import com.muei.travelmate.ui.auth.User
import com.muei.travelmate.ui.image.CircleTransform
import com.squareup.picasso.Picasso


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var toggleButton: ToggleButton

    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var imgView: ImageView



    companion object {
        lateinit var account: Auth0
        var userIsAuthenticated: Boolean = false
        lateinit var user: User
        var currentNightMode = AppCompatDelegate.getDefaultNightMode()


    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        _binding = FragmentUserBinding.inflate(inflater, container, false)

        //** AUTH0 Setup
        // Account details
        account = Auth0(
            getString(R.string.auth0_client_id),
            getString(R.string.auth0_domain)
        )

        validateLocalJWT()


        //** UI elementss
        loginButton = binding.root.findViewById(R.id.user_login_button)
        logoutButton = binding.root.findViewById(R.id.user_logout_button)

        userName = binding.root.findViewById(com.muei.travelmate.R.id.userName)
        userEmail = binding.root.findViewById(com.muei.travelmate.R.id.userMail)
        imgView = binding.root.findViewById(com.muei.travelmate.R.id.imageView2)

        loginButton.setOnClickListener {
            login()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        updateUI()

        toggleButton = binding.root.findViewById(R.id.toggleButton)

        toggleButton.setOnClickListener{
            changeMode()
        }
        updateMode()

        return binding.root
    }


    fun validateLocalJWT() {
        //applicationContext------------
        val _idToken = readFromSharedPreferences(binding.root.context, "id_token", "")
        // If there is a JWT id stored
        if (_idToken != "") {
            // check if the token has expired
            user = User(_idToken)
            if (!user.expired) {
                // I can use this local token to login
                userIsAuthenticated = true
            }
        }
    }

    fun login() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account).withScheme(getString(R.string.auth0_scheme))
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    //showSnackbar(exception.message)
                }

                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    userIsAuthenticated = true

                    val idToken: String = credentials.idToken

                    // store idToken on SharedPreferences
                    writeToSharedPreferences(requireContext(), "id_token", idToken)

                    user = User(idToken)
                    updateUI()
                }
            })
    }

    fun logout() {
        WebAuthProvider.logout(account).withScheme(getString(R.string.auth0_scheme))
            .start(requireContext(), object : Callback<Void?, AuthenticationException> {
                // Called when there is a failure
                override fun onFailure(error: AuthenticationException) {
                    //showSnackbar(getString(R.string.logout_failure_message, error.getCode()))
                }

                // Called when authentication completed successfully
                override fun onSuccess(result: Void?) {
                    userIsAuthenticated = false
                    writeToSharedPreferences(requireContext(), "id_token", "")
                    updateUI()
                }
            })

    }

    fun writeToSharedPreferences(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences("travelmate_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(key, value)
            apply()
        }
    }

    // Función para leer desde SharedPreferences
    fun readFromSharedPreferences(context: Context, key: String, defaultValue: String): String {
        val sharedPref = context.getSharedPreferences("travelmate_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    private fun updateUI() {

        if (userIsAuthenticated) {
            loginButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            loginButton.isEnabled = false
            logoutButton.isEnabled = true

            userName.text = user.name
            userEmail.text = user.email
            Picasso.get().load(user.picture).transform(CircleTransform()).into(imgView)

        } else {
            logoutButton.visibility = View.GONE
            loginButton.visibility = View.VISIBLE
            loginButton.isEnabled = true
            logoutButton.isEnabled = false

            userName.text = "Welcome!!"
            userEmail.text = ""
            Picasso.get()
                .load("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ficon-library.com%2Fimages%2Fgeneric-user-icon%2Fgeneric-user-icon-19.jpg")
                .transform(CircleTransform()).into(imgView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun changeMode() {
        currentNightMode = AppCompatDelegate.getDefaultNightMode()

        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            // Currently in day mode, switching to night mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Currently in night mode, switching to day mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        updateMode()
    }

    fun updateMode(){
        toggleButton.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }
}
