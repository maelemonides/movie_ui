package com.example.movieui.core.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.movieui.module.detail.presentation.DetailScreen
import com.example.movieui.module.home.model.MovieModel
import com.example.movieui.module.home.model.nowPlayingMovie
import com.example.movieui.module.home.model.upcoming
import com.example.movieui.module.home.presentation.FavoritesScreen
import com.example.movieui.module.home.presentation.HomeScreen

object AppRoute {

    const val KEY_ROUTE = "route"

    @Composable
    fun GenerateRoute(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = AppRouteName.Home,
        ) {
            composable(AppRouteName.Home) {
                HomeScreen(navController = navController)
            }
            composable("${AppRouteName.Detail}/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val movie = findMovieById(id)

                if (movie != null) {
                    DetailScreen(navController = navController, movie)
                } else {
                    // Handle the case where the movie with the given ID is not found
                    // For example, display an error message or navigate to a fallback screen
                }
            }
            composable(AppRouteName.Favorites) {
                FavoritesScreen(navController = navController)
            }
        }
    }

    // Helper function to find a movie by its ID in both nowPlayingMovie and upcoming lists
    private fun findMovieById(id: String?): MovieModel? {
        val allMovies = nowPlayingMovie + upcoming
        return allMovies.find { it.id == id }
    }
}
