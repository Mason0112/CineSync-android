package com.mason.cinesync.service

import com.mason.cinesync.model.dto.PopularMovieApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US"
    ): PopularMovieApiResponse


}