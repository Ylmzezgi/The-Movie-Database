package com.ezgiyilmaz.tmdi.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.ezgiyilmaz.tmdi.api.ApiModel
import com.ezgiyilmaz.tmdi.api.ApiService
import com.ezgiyilmaz.tmdi.api.MovieDetailResponse
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.databinding.ActivityMovieDetailBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private lateinit var auth: FirebaseAuth
    private var BASE_URL = "Base_url"
    private val db = FirebaseFirestore.getInstance()
    val favList: ArrayList<ApiModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        auth = FirebaseAuth.getInstance()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        loadMovieDetails(movieId)
        nowMovieDetails(movieId)
    }


    private fun loadMovieDetails(movieId: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getMovieDetails(movieId)

        call.enqueue(object : Callback<MovieDetailResponse> {

            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val movieDetail = response.body()
                    movieDetail?.let {

                        binding.filmTitle.text = it.original_title
                        binding.releaseDate.text = it.release_date
                        binding.filmOverview.text = it.overview

                        val postUrl = "imagesUrl${it.poster_path}"
                        Glide.with(this@MovieDetailActivity)
                            .load(postUrl)
                            .into(binding.imageView)
                        val movieId = movieDetail.id
                        intent.putExtra("MOVİE_ID", movieId)
                    }
                } else {
                    Log.e(
                        "MovieDetailActivity",
                        "API yanıtı başarısız - Hata Kodu: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun nowMovieDetails(movieId: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getMoviesDetailsNow(movieId)

        call.enqueue(object : Callback<MovieDetailResponse> {

            override fun onResponse(
                call: Call<MovieDetailResponse>,
                response: Response<MovieDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val movieDetail = response.body()
                    movieDetail?.let {
                        binding.filmTitle.text = it.original_title
                        binding.releaseDate.text = it.release_date
                        binding.filmOverview.text = it.overview

                        val postUrl = "imagesUrl${it.poster_path}"
                        Glide.with(this@MovieDetailActivity)
                            .load(postUrl)
                            .into(binding.imageView)
                        intent.putExtra("POSTER_PATH", movieDetail.poster_path)
                    }
                }

            }

            override fun onFailure(call: Call<MovieDetailResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    fun favOnClick(view: View) {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Kullanıcı giriş yapmışsa işlemlere devam et
            val movieId = intent.getIntExtra("MOVIE_ID", -1)
            val movieTitle = binding.filmTitle.text.toString()
            val movieDescription = binding.filmOverview.text.toString()
            val movieDate = binding.releaseDate.text.toString()
            val posterUrl = "https://image.tmdb.org/t/p/original/${intent.getStringExtra("poster_path")}"

            val favoriteMovie = mapOf(
                "id" to movieId,
                "title" to movieTitle,
                "description" to movieDescription,
                "movieDate" to movieDate,
                "posterUrl" to posterUrl
            )

            // Film veritabanında mevcut mu kontrol et
            val query = db.collection("favorities")
                .document(currentUser.uid) // Kullanıcıya özgü favori koleksiyonu
                .collection("movies")
                .whereEqualTo("id", movieId)
                .limit(1)

            query.get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        db.collection("favorities")
                            .document(currentUser.uid) // Kullanıcıya özgü favori koleksiyonu
                            .collection("movies")
                            .add(favoriteMovie)
                            .addOnSuccessListener {
                                startActivity(Intent(this, FavoritiesPage::class.java))
                                Toast.makeText(this, "Film favorilere eklendi!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        startActivity(Intent(this, FavoritiesPage::class.java))
                        Toast.makeText(this, "Film zaten favorilere eklenmiş.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        } else {
            val snackbar = Snackbar.make(view, "Favorilere eklemek için lütfen giriş yapın", Snackbar.LENGTH_LONG)
            snackbar.setAction("Giriş Yap") {
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
            }
            snackbar.show()
        }
    }


    fun watchOnClick(view: View) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val movieId = intent.getIntExtra("MOVİE_ID", -1)
            val movieTitle = binding.filmTitle.text.toString()
            val movieDescription = binding.filmOverview.text.toString()
            val movieDate = binding.releaseDate.text.toString()
            val postUrl =
                "https://image.tmdb.org/t/p/original/${intent.getStringExtra("poster_path")}"

            val watchMovie = mapOf(
                "id" to movieId,
                "id" to movieId,
                "title" to movieTitle,
                "description" to movieDescription,
                "movieDate" to movieDate,
                "posterUrl" to postUrl
            )

            val query = db.collection("favorities")
                .whereEqualTo("id", movieId)
                .limit(1)
            query.get().addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    db.collection("favorities")
                        .add(watchMovie)
                        .addOnSuccessListener {
                            startActivity(Intent(this, WatchPage::class.java))
                            Toast.makeText(this, "film izleme listesine eklendi", Toast.LENGTH_LONG)
                                .show()

                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "hata : ${e.localizedMessage}", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    Toast.makeText(this, "film veritabanına kaydedildi", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, WatchPage::class.java))
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "hata: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }else{

            val snackbar = Snackbar.make(view, "İzleme listesine eklemek için lütfen giriş yapın", Snackbar.LENGTH_LONG)
            snackbar.setAction("Giriş Yap") {
                startActivity (Intent(this, LoginPage::class.java))
            }
            snackbar.show()
        }
    }
}










































