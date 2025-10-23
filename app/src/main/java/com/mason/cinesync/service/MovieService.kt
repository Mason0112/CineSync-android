package com.mason.cinesync.service

import com.mason.cinesync.model.dto.MovieDetailResponse
import com.mason.cinesync.model.dto.PopularMovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("/api/movies/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): PopularMovieApiResponse


    @GET("/api/movies/detail/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US"
    ): MovieDetailResponse


}