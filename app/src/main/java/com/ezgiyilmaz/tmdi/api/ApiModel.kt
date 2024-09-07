package com.ezgiyilmaz.tmdi.api
data class MovieResponse(
    val results: List<ApiModel>
)

data class ApiModel(
    val id: Int = 0,
    val original_title: String = "",
    val overview: String = "",
    val release_date: String = "",
    val poster_path: String = ""
)
data class MovieDetailResponse(
    val id: Int,
    val original_title: String,
    val overview: String,
    val release_date: String,
    val poster_path: String
)
