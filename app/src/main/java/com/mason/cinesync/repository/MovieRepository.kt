package com.mason.cinesync.repository

import android.util.Log
import com.mason.cinesync.model.dto.MovieDetailResponse
import com.mason.cinesync.model.dto.PopularMovieApiResponse
import com.mason.cinesync.service.MovieService

class MovieRepository(private val movieService: MovieService) {

    val TAG = MovieRepository::class.java.simpleName

    suspend fun getPopularMovies(page: Int, language: String): PopularMovieApiResponse {
        val popularMovies = movieService.getPopularMovies(page, language)
        Log.d(TAG, "Fetched popular movies: ${popularMovies.results.size}")
        return popularMovies
    }

    suspend fun getMovieDetails(movieId: Int, language: String): MovieDetailResponse {
        val movieDetails = movieService.getMovieDetails(movieId, language)
        return movieDetails
    }
}
