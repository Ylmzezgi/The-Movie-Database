package com.ezgiyilmaz.tmdi.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.tmdi.Adapter.MovieAdapter
import com.ezgiyilmaz.tmdi.api.ApiModel
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.databinding.ActivityWatchPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WatchPage : AppCompatActivity() {
    private lateinit var binding:ActivityWatchPageBinding
    private var db=FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var watchAdapter: MovieAdapter
    private lateinit var watchList: ArrayList<ApiModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityWatchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
         ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        watchList=ArrayList()
        watchAdapter= MovieAdapter(watchList) { movie ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("favorities").document(movie.id.toString()).delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "${movie.original_title} izleme listesinden kaldırıldı",
                            Toast.LENGTH_LONG
                        ).show()
                        watchList.remove(movie)
                        watchAdapter.notifyDataSetChanged()
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }
        binding.reclerView.layoutManager=LinearLayoutManager(this)
        binding.reclerView.adapter=watchAdapter
        verileriAl()
    }

    private fun verileriAl(){
        val currentuser=auth.currentUser
        if(currentuser!=null) {
            db.collection("favorities").addSnapshotListener { value, error ->
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (document in documents) {
                            val id = document.getLong("id")?.toInt() ?: 0
                            val title = document.getString("title") ?: ""
                            val description = document.getString("description") ?: ""
                            val movieDate = document.getString("movieDate") ?: ""
                            val postUrl = document.getString("posterUrl") ?: ""

                            val movie = ApiModel(id, title, description, movieDate, postUrl)
                            watchList.add(movie)
                        }
                        watchAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

}