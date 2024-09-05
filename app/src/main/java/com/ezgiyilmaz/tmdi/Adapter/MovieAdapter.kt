package com.ezgiyilmaz.tmdi.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ezgiyilmaz.tmdi.api.ApiModel
import com.ezgiyilmaz.tmdi.databinding.ItemMovieBinding

class MovieAdapter(private val movies: List<ApiModel>, private val onItemClick: (ApiModel) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: ApiModel) {
            binding.movieTitle.text = movie.original_title
            binding.realsedate.text=movie.release_date
            val posterUrl = "images_url${movie.poster_path}"
            Glide.with(binding.root.context)
                .load(posterUrl)
                .into(binding.posterPath)
            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])

    }

    override fun getItemCount() = movies.size

}