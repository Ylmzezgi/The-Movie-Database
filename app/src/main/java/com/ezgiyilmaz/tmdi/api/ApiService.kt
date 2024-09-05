package com.ezgiyilmaz.tmdi.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    //"https://api.themoviedb.org/3/movie/upcoming?api_key=b158fc1e10a7ef9b8d6bd14254fe0831"
    //https://api.themoviedb.org/3/movie/now_playing?api_key=b158fc1e10a7ef9b8d6bd14254fe0831
    @GET("Your_Key")
    fun getDataUpcoming(): Call<MovieResponse>

    @GET("Your_key")
    fun getDataNowPlaying(): Call<MovieResponse>



        @GET("Your_key")
        fun getMovieDetails(@Path("movie_id") movieId: Int): Call<MovieDetailResponse>



    @GET("Your_key")
    fun getMoviesDetailsNow(@Path("movie_id") movieId:Int):Call<MovieDetailResponse>

}