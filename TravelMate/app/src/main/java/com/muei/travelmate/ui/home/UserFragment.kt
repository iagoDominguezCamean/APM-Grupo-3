package com.muei.travelmate.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.muei.travelmate.databinding.FragmentUserBinding
import com.muei.travelmate.ui.auth.User
import com.muei.travelmate.ui.image.CircleTransform
import com.squareup.picasso.Picasso


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(UserViewModel::class.java)

        _binding = FragmentUserBinding.inflate(inflater, container, false)


        val idToken = readFromSharedPreferences(requireContext(), "id_token", "")
        // If there is a JWT id stored
        if(idToken!=""){
            // check if the token has expired
            val user = User(idToken)
            val userName : TextView = binding.root.findViewById(com.muei.travelmate.R.id.userName)
            userName.text=user.name
            val userEmail : TextView = binding.root.findViewById(com.muei.travelmate.R.id.userMail)
            userEmail.text=user.email
            val imgView: ImageView = binding.root.findViewById(com.muei.travelmate.R.id.imageView2)
            Picasso.get().load(user.picture).transform(CircleTransform()).into(imgView)


            //cargarImagenUsuario( binding.root.findViewById(R.id.imageView2),user.picture)

            // Glide






        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun readFromSharedPreferences(context: Context, key: String, defaultValue: String): String {
        val sharedPref = context.getSharedPreferences("travelmate_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }
}
