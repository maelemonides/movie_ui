package com.example.hitemamovie.data.repository

import com.example.hitemamovie.api.TmdbApi
import com.example.movieui.module.home.model.MovieModel

class MovieRepository(private val tmdbApi: TmdbApi) {
    suspend fun getPopularMovies(): List<MovieModel> {
        val response = tmdbApi.getPopularMovies()
        return if (response.isSuccessful) {
            response.body()?.results ?: emptyList()
        } else {
            emptyList()
        }
    }
}