package com.mason.cinesync.repository

import com.mason.cinesync.model.Result
import com.mason.cinesync.model.dto.PopularMovieApiResponse
import com.mason.cinesync.service.MovieService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(private val movieService: MovieService) {

    fun getPopularMoviesStream(page: Int, language: String): Flow<Result<PopularMovieApiResponse>> = flow {
        emit(Result.Loading)
        try {
            val popularMovies = movieService.getPopularMovies(page, language)
            emit(Result.Success(popularMovies))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
