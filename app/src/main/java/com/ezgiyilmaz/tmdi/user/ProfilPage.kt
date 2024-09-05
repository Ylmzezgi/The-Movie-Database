package com.ezgiyilmaz.tmdi.user

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.databinding.ActivityProfilPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ProfilPage : AppCompatActivity() {
    private lateinit var binding: ActivityProfilPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        val user = Firebase.auth.currentUser
        user?.let {
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            binding.adSoyadText.text = name
            binding.emailText.text = email

            if (photoUrl != null) {
                Picasso.get()
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Yüklenirken gösterilecek resim
                    .error(R.drawable.splash_logo) // Yüklenemezse gösterilecek resim
                    .into(binding.imageView3)
            }
                Log.e("ProfilPage","${photoUrl}" )
            Log.e("ProfilPage","${name}" )
        }
    }
}