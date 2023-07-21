package com.example.movieui.module.home.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.movieui.R
import com.example.movieui.core.route.AppRoute.KEY_ROUTE
import com.example.movieui.core.route.AppRouteName
import com.example.movieui.core.theme.BlueVariant
import com.example.movieui.module.home.model.MovieModel
import com.example.movieui.module.home.model.nowPlayingMovie
import com.example.movieui.module.home.model.upcoming
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    top = padding.calculateTopPadding() + 24.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
                )
        ) {
            Text(
                text = "Welcome back !",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Browse your favourite movies",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Now Playing Movie",
                    style = MaterialTheme.typography.h6,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            NowPlayingMovie { movie ->
                navController.navigate("${AppRouteName.Detail}/${movie.id}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Upcoming Movie",
                    style = MaterialTheme.typography.h6,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            UpcomingMovie { movie ->
                navController.navigate("${AppRouteName.Detail}/${movie.id}")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, AppRouteName.Home),
        BottomNavItem("Favorites", Icons.Default.Favorite, AppRouteName.Favorites)
    )

    BottomNavigation(
        modifier = Modifier.background(color = Color.White)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    // This is where you handle the navigation when a tab is clicked.
                    navController.navigate(item.route)
                }
            )
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    val favoriteState = remember { mutableStateOf(isFavorite) }

    IconButton(
        onClick = {
            favoriteState.value = !favoriteState.value
            onClick()
        }
    ) {
        Icon(
            imageVector = if (favoriteState.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (favoriteState.value) "Remove from favorites" else "Add to favorites",
            tint = if (favoriteState.value) Color.Red else MaterialTheme.colors.onSurface
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Favorites") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            FavoritesMovieList(navController)
        }
    )
}

@Composable
fun FavoritesMovieList(navController: NavHostController) {
    val favoriteMovies = nowPlayingMovie.filter { it.isFavorite } + upcoming.filter { it.isFavorite }

    // Display a list of favorited movies using the same style as the Upcoming movies
    LazyRow(contentPadding = PaddingValues(start = 24.dp)) {
        items(favoriteMovies) { movie ->
            Box(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .clickable {
                        navController.navigate("${AppRouteName.Detail}/${movie.id}")
                    }
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = movie.assetImage),
                        contentDescription = "Movie Image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.size(width = 200.dp, height = 260.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Add the FavoriteButton here
                    FavoriteButton(
                        isFavorite = movie.isFavorite,
                        onClick = {
                            movie.isFavorite = !movie.isFavorite
                        }
                    )
                }
            }
        }
    }
}


data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NowPlayingMovie(
    onMovieClicked: (MovieModel) -> Unit
) {
    HorizontalPager(
        count = nowPlayingMovie.size,
        contentPadding = PaddingValues(start = 48.dp, end = 48.dp)
    ) { page ->
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .graphicsLayer {
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                    lerp(
                        start = ScaleFactor(1f, 0.85f),
                        stop = ScaleFactor(1f, 1f),
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale.scaleX
                        scaleY = scale.scaleY
                    }
                }
                .clickable {
                    onMovieClicked(nowPlayingMovie[page])
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onMovieClicked(nowPlayingMovie[page]) }
            ) {
                Image(
                    painter = painterResource(id = nowPlayingMovie[page].assetImage),
                    contentDescription = "Movie Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.85f)
                        .height(340.dp)
                )

                // FavoriteButton placed at the bottom-right corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    FavoriteButton(
                        isFavorite = nowPlayingMovie[page].isFavorite,
                        onClick = {
                            nowPlayingMovie[page].isFavorite = !nowPlayingMovie[page].isFavorite
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = nowPlayingMovie[page].title,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun UpcomingMovie(onMovieClicked: (MovieModel) -> Unit) {
    LazyRow(contentPadding = PaddingValues(start = 24.dp)) {
        items(upcoming) { movie ->
            Box(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .clickable {
                        onMovieClicked(movie) // Pass the clicked movie to the navigation callback
                    }
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = movie.assetImage),
                        contentDescription = "Movie Image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.size(width = 200.dp, height = 260.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Add the FavoriteButton here
                    FavoriteButton(
                        isFavorite = movie.isFavorite,
                        onClick = {
                            movie.isFavorite = !movie.isFavorite
                        }
                    )
                }
            }
        }
    }
}