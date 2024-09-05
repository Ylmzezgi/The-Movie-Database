package com.ezgiyilmaz.tmdi.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ezgiyilmaz.tmdi.main.MainActivity
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.view.LoginPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class userInformationPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_information_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth=Firebase.auth
    }

    fun KullanıcıBilgileriOnClick(view: View){
        val intent= Intent(this, ProfilPage::class.java)
        startActivity(intent)
    }

    fun SignOutOnClick(view: View){
        auth.signOut()
        Toast.makeText(this, "Başarıyla çıkış yapıldı", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun girişOnClick(view: View){
        startActivity(Intent(this, LoginPage::class.java))
    }
}