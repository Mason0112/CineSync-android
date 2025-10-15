package com.mason.cinesync.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mason.cinesync.model.Result
import com.mason.cinesync.viewmodel.PopularMoviesViewModel
import com.mason.cinesync.viewmodel.PopularMoviesViewModelFactory

@Composable
fun PopularMoviesScreen(
    viewModel: PopularMoviesViewModel = viewModel(factory = PopularMoviesViewModelFactory())
) {
    val moviesState by viewModel.movies.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = moviesState) {
            is Result.Loading -> {
                CircularProgressIndicator()
            }
            is Result.Success -> {
                // For now, just display the number of movies.
                // Later, we can build a LazyColumn here.
                Text("Successfully loaded ${state.data.results.size} movies.")
            }
            is Result.Error -> {
                Text("Error: ${state.exception.message}")
            }
        }
    }
}
