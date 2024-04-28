package com.muei.travelmate.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar
import com.muei.travelmate.R
import com.muei.travelmate.ui.MainActivity
import com.muei.travelmate.ui.auth.User

class AuthActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button

    companion object {
        lateinit var account: Auth0
        var userIsAuthenticated: Boolean = false
        lateinit var user: User
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.auth0_client_id),
            getString(R.string.auth0_domain)
        )

        // Botones de login y logout
        loginButton = findViewById(R.id.login_button)
        logoutButton = findViewById(R.id.logout_button)

        loginButton.setOnClickListener {
            login()
        }

        logoutButton.setOnClickListener {
            logout()
        }

        validateLocalJWT()
        updateViewOnLogAction()
    }

    fun validateLocalJWT() {
        val _idToken = readFromSharedPreferences(applicationContext, "id_token", "")
        // If there is a JWT id stored
        if (_idToken != "") {
            // check if the token has expired
            user = User(_idToken)
            if (!user.expired) {
                // I can use this local token to login
                userIsAuthenticated = true
                // Temp: Opens the home fragment
                navigateToHome()
            }
        }
    }

    fun login() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account).withScheme(getString(R.string.auth0_scheme))
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    showSnackbar(exception.message)
                }

                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    userIsAuthenticated = true

                    val idToken: String = credentials.idToken

                    // store idToken on SharedPreferences
                    writeToSharedPreferences(applicationContext, "id_token", idToken)

                    user = User(idToken)
                    updateViewOnLogAction()

                    // Temp: Opens the home fragment
                    navigateToHome()
                }
            })
    }

    fun logout() {
         WebAuthProvider.logout(account).withScheme(getString(R.string.auth0_scheme))
            .start(this, object : Callback<Void?, AuthenticationException> {
                // Called when there is a failure
                override fun onFailure(error: AuthenticationException) {
                    showSnackbar(getString(R.string.logout_failure_message, error.getCode()))
                }

                // Called when authentication completed successfully
                override fun onSuccess(result: Void?) {
                    userIsAuthenticated = false
                    writeToSharedPreferences(applicationContext, "id_token", "")
                    updateViewOnLogAction()
                }
            })

    }

    fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(message: String?) {
        Snackbar.make(findViewById(android.R.id.content), message as CharSequence, Snackbar.LENGTH_SHORT).show()
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

    fun updateViewOnLogAction() {
        if (userIsAuthenticated) {
            findViewById<TextView>(R.id.loginview_text).text = getString(
                R.string.login_success_message,
                user.name ?: ""
            )
            //loginButton.visibility = View.GONE
            //logoutButton.visibility = View.VISIBLE
            loginButton.isEnabled = false
            logoutButton.isEnabled = true
        } else {
            //logoutButton.visibility = View.GONE
            //loginButton.visibility = View.VISIBLE
            findViewById<TextView>(R.id.loginview_text).text = getString(R.string.login_welcome)
            loginButton.isEnabled = true
            logoutButton.isEnabled = false
        }
    }

    private fun navigateToHome() {
        // Start the new activity here
        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // Finish current activity if needed
    }
}
