package com.example.hitemamovie.api

import com.example.movieui.module.home.model.ApiResponse
import com.example.movieui.module.home.model.MovieModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface TmdbApi {
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIxYWMwMWQwNThlYzljMDBhYmFiYTQwN2E0MDVhZGE5MyIsInN1YiI6IjYzZmY1MTIyOTY1M2Y2MDBiNThiOTE3OCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.yz_tMi39k4L-td563CunwReO-ViMWa9YqEuleW7sQFo")
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "fr-FR",
        @Query("page") page: Int = 1
    ): retrofit2.Response<ApiResponse<MovieModel>>
}

fun createTmdbApi(): TmdbApi {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(TmdbApi::class.java)
}