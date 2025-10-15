package com.mason.cinesync.repository

import android.util.Log
import com.mason.cinesync.model.Result
import com.mason.cinesync.model.dto.PopularMovieApiResponse
import com.mason.cinesync.service.MovieService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(private val movieService: MovieService) {

    val TAG = MovieRepository::class.java.simpleName

    fun getPopularMoviesStream(page: Int, language: String): Flow<Result<PopularMovieApiResponse>> = flow {
        emit(Result.Loading)
        try {
            val popularMovies = movieService.getPopularMovies(page, language)
            Log.d(TAG, "Fetched popular movies: $popularMovies")
            emit(Result.Success(popularMovies))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
