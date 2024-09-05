package com.ezgiyilmaz.tmdi.main
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ezgiyilmaz.tmdi.Adapter.MovieAdapter
import com.ezgiyilmaz.tmdi.api.ApiService
import com.ezgiyilmaz.tmdi.api.MovieResponse
import com.ezgiyilmaz.tmdi.R
import com.ezgiyilmaz.tmdi.view.WatchPage
import com.ezgiyilmaz.tmdi.databinding.ActivityMainBinding
import com.ezgiyilmaz.tmdi.user.userInformationPage
import com.ezgiyilmaz.tmdi.view.FavoritiesPage
import com.ezgiyilmaz.tmdi.view.MovieDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val BASE_URL = "Base_Url"
    private lateinit var upcomingAdapter: MovieAdapter
    private lateinit var nowPlayingAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewId.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)

        loadData()
        nowData()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritiesPage::class.java))
                }


                R.id.nav_watchlist -> {
                    startActivity(Intent(this, WatchPage::class.java))
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, userInformationPage::class.java))
                }
            }

            true


        }}

    private fun loadData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getDataUpcoming()

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if(response.isSuccessful){
                    val movies = response.body()?.results ?: emptyList()
                    upcomingAdapter = MovieAdapter(movies){movieItem ->
                        val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                        intent.putExtra("MOVIE_ID", movieItem.id)
                        intent.putExtra("poster_path",movieItem.poster_path)
                        startActivity(intent)
                    }
                    binding.recyclerView.adapter = upcomingAdapter
                } else {
                    Log.e("TAG", "API yanıtı başarısız - Hata Kodu: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("TAG", "API çağrısı başarısız - Hata Mesajı: ${t.message}")
            }
        })
    }

    private fun nowData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getDataNowPlaying()

        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if(response.isSuccessful){
                    val movies = response.body()?.results ?: emptyList()
                    nowPlayingAdapter = MovieAdapter(movies){movieItem->
                        val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
                        intent.putExtra("MOVIE_ID", movieItem.id)
                        intent.putExtra("poster_path",movieItem.poster_path)
                        startActivity(intent)
                    }
                    binding.recyclerViewId.adapter = nowPlayingAdapter
                } else {
                    Log.e("TAG", "API yanıtı başarısız - Hata Kodu: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("TAG", "API çağrısı başarısız - Hata Mesajı: ${t.message}")
            }
        })
    }
}
