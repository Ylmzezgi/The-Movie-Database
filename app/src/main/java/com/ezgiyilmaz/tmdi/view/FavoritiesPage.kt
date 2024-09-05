package com.ezgiyilmaz.tmdi.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.tmdi.api.ApiModel
import com.ezgiyilmaz.tmdi.Adapter.MovieAdapter
import com.ezgiyilmaz.tmdi.databinding.ActivityFavoritiesPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritiesPage : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritiesPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var favList: ArrayList<ApiModel>
    private lateinit var favoritesAdapter: MovieAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritiesPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        favList = ArrayList()
        favoritesAdapter = MovieAdapter(favList) { movie ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("favorities").document(movie.id.toString()).delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "${movie.original_title} izleme listesinden kaldırıldı",
                            Toast.LENGTH_LONG
                        ).show()
                        favList.remove(movie)
                        favoritesAdapter.notifyDataSetChanged()
                    }.addOnFailureListener {
                        it.printStackTrace()
                    }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = favoritesAdapter

        verileriAl()
    }

    private fun verileriAl() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("favorities")
                .document(currentUser.uid)
                .collection("movies")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            favList.clear()
                            for (document in documents) {
                                val id = document.getLong("id")?.toInt() ?: 0
                                val title = document.getString("title") ?: ""
                                val description = document.getString("description") ?: ""
                                val movieDate = document.getString("movieDate") ?: ""
                                val postUrl = document.getString("posterUrl") ?: ""

                                val movie = ApiModel(id, title, description, movieDate, postUrl)
                                favList.add(movie)
                            }
                            favoritesAdapter.notifyDataSetChanged()
                        }
                    }
                }
        } else {
            Toast.makeText(this, "Favori filmleri görmek için lütfen giriş yapın", Toast.LENGTH_SHORT).show()
        }
    }
}
