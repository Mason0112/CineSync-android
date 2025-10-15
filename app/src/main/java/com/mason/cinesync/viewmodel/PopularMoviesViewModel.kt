package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.cinesync.model.Result
import com.mason.cinesync.model.dto.PopularMovieApiResponse
import com.mason.cinesync.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PopularMoviesViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<Result<PopularMovieApiResponse>>(Result.Loading)
    val movies: StateFlow<Result<PopularMovieApiResponse>> = _movies.asStateFlow()

    init {
        // Fetch the first page when the ViewModel is created
        fetchPopularMovies(1, "en-US")
    }

    fun fetchPopularMovies(page: Int, language: String) {
        viewModelScope.launch {
            movieRepository.getPopularMoviesStream(page, language)
                .collect { result ->
                    _movies.value = result
                }
        }
    }
}
