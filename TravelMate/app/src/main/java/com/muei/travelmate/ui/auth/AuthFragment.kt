package com.muei.travelmate.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.muei.travelmate.R
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.google.android.material.snackbar.Snackbar
import com.muei.travelmate.databinding.FragmentAuthBinding


class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private lateinit var account: Auth0

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var authViewModel: AuthViewModel;
    private var userIsAuthenticated: Boolean = false
    private lateinit var user : User;
    // private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            getString(R.string.auth0_client_id),//"@string/auth0_client_id",
            getString(R.string.auth0_domain)//"@string/auth0_domain"
        )
    }

    /*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }
    */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Botones de login y logout
        loginButton = binding.loginButton
        logoutButton = binding.logoutButton

        loginButton.setOnClickListener {
            login()
        }

        logoutButton.setOnClickListener {
            logout()
        }/*
                loginButton.setOnClickListener {
                    onLoginButtonClick()
                }

                logoutButton.setOnClickListener {
                    onLogoutButtonClick()
                }

                // Cargo datos almacenados localmente
                homeViewModel.user = readFromSharedPreferences(requireContext(), "user", "")
                showToast(homeViewModel.user)

                // Actualizo UI
                updateViewOnLogAction()
                //

                val textView: TextView = binding.textHome
                homeViewModel.text.observe(viewLifecycleOwner) {
                    textView.text = it
                }


             */

        updateViewOnLogAction()
        return root
    }

    private fun login() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account).withScheme(getString(R.string.auth0_scheme))
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    showSnackbar(exception.message)
                }

                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    userIsAuthenticated = true
                    //val accessToken = credentials.accessToken

                    user = User(credentials.idToken)
                    updateViewOnLogAction()

                }
            })
    }


    private fun logout() {
        WebAuthProvider.logout(account).withScheme(getString(R.string.auth0_scheme))
            .start(requireContext(), object : Callback<Void?, AuthenticationException> {
                // Called when there is a failure
                override fun onFailure(error: AuthenticationException) {
                    showSnackbar(getString(R.string.logout_failure_message,
                                            error.getCode()))
                }

                // Called when authentication completed successfully
                override fun onSuccess(result: Void?) {
                    userIsAuthenticated = false
                    updateViewOnLogAction()
                }
            })

    }

    fun showToast(message: String?) {

        val context = context ?: return // Check if context is null
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(message: String?) {
        Snackbar.make(binding.root, message as CharSequence, Snackbar.LENGTH_SHORT).show()
    }

    fun updateViewOnLogAction(){
        if(userIsAuthenticated) {
            binding.loginviewText.text=getString(R.string.login_success_message,
                user.name?:"");
            //loginButton.visibility = View.GONE
            //logoutButton.visibility = View.VISIBLE
            loginButton.isEnabled = false
            logoutButton.isEnabled = true
        }else{
            //logoutButton.visibility = View.GONE
            //loginButton.visibility = View.VISIBLE
            binding.loginviewText.text=getString(R.string.login_welcome)
            loginButton.isEnabled = true
            logoutButton.isEnabled = false
        }
    }

}