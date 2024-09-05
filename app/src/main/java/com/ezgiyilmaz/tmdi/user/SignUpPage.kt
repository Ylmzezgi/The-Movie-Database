package com.ezgiyilmaz.tmdi.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ezgiyilmaz.tmdi.main.MainActivity
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.databinding.ActivitySignUpPageBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class SignUpPage : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySignUpPageBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)


        auth = Firebase.auth
        storage = FirebaseStorage.getInstance()

        db = FirebaseFirestore.getInstance()

        registerLauncher()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    fun imageview(view: View) {
        binding.imageView4.visibility = View.VISIBLE
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                Snackbar.make(view, "İzin gerekli", Snackbar.LENGTH_INDEFINITE)
                    .setAction("İzin ver") {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding.imageView4.setImageURI(it)
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(this, "İzin verilmedi", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun kayıtOnClick(view: View) {
        val isim = binding.editTextName.text.toString()
        val soyisim = binding.editTextsurname.text.toString()
        val email = binding.editTextEmailAddress.text.toString()
        val sifre = binding.editTextPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, sifre).addOnSuccessListener {
            val user = auth.currentUser
            if (user != null) {
                val displayName = "$isim $soyisim"
                val uuid = UUID.randomUUID()
                val gorselAdi = "$uuid.jpg"

                val reference = storage.reference
                val gorselReferansi = reference.child("images").child(gorselAdi)

                if (selectedPicture != null) {
                    gorselReferansi.putFile(selectedPicture!!).addOnSuccessListener {
                        gorselReferansi.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            val profileUpdates = userProfileChangeRequest {
                                this.displayName = displayName
                                this.photoUri = Uri.parse(downloadUrl)
                            }

                            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("FirebaseAuth", "Profil güncellendi: İsim - $displayName, Fotoğraf URL - $downloadUrl")
                                    Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.e("FirebaseAuth", "Profil güncellenemedi: ${task.exception?.message}")
                                    Toast.makeText(this, "Profil güncellenemedi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("FirebaseStorage", "Resim URL'si alınamadı: ${exception.localizedMessage}")
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("FirebaseStorage", "Resim yüklenemedi: ${exception.localizedMessage}")
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("FirebaseAuth", "Kayıt başarısız: ${exception.localizedMessage}")
            Toast.makeText(this, "Kayıt başarısız: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }


}