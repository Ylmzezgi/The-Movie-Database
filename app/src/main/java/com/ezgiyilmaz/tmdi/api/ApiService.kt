package com.ezgiyilmaz.tmdi.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("Your_Key")
    fun getDataUpcoming(): Call<MovieResponse>

    @GET("Your_key")
    fun getDataNowPlaying(): Call<MovieResponse>



        @GET("Your_key")
        fun getMovieDetails(@Path("movie_id") movieId: Int): Call<MovieDetailResponse>



    @GET("Your_key")
    fun getMoviesDetailsNow(@Path("movie_id") movieId:Int):Call<MovieDetailResponse>

}
