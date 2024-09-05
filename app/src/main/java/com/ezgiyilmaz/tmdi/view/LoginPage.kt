package com.ezgiyilmaz.tmdi.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ezgiyilmaz.tmdi.main.MainActivity
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.user.SignUpPage
import com.ezgiyilmaz.tmdi.databinding.ActivityLoginPageBinding
import com.ezgiyilmaz.tmdi.user.userInformationPage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPage : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 9001

    }
    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        val curentUser =
            auth.currentUser // daha önceden giriş yapılıp yapılmadığını öğrenip giriş yapıldıysa istenilen sayfaya gitsin
        if (curentUser != null) {
            val intent = Intent(this, userInformationPage::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

       //kullanıcı giriş yaptıktan sonra maine gitsin
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("TAG", "User is already signed in: ${currentUser.email}")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val signInButton = findViewById<Button>(R.id.googleButon)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        Log.d("TAG", "Initiating Google sign-in")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Log.d("TAG", "Google sign-in result received")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("TAG", "Google sign-in successful, ID Token: ${account?.idToken}")
                firebaseAuthWithGoogle(account?.idToken!!)
            } catch (e: ApiException) {
                Log.e("TAG", "Google sign-in failed", e)
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d("TAG", "Authenticating with Firebase using Google ID Token")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d("TAG", "Signed in as ${user?.email}")
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "signInWithCredential failed", e)
                Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun loginOnClick(view: View) {
        try {
            val email = binding.editTextEmailAddress.text.toString()
            val sifre = binding.editTextNumberPassword.text.toString()
            if (email.equals("") || sifre.equals("")) {
                Toast.makeText(this, "Email ve şifre giriniz", Toast.LENGTH_LONG).show()
            } else {
                auth.signInWithEmailAndPassword(email, sifre).addOnSuccessListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.localizedMessage
        }
    }
        fun HesapOlusturOnClick(view: View) {
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
            finish()
        }
    }


























