package com.mason.cinesync.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MovieApiResponse(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Double
)
@Serializable
data class PopularMovieApiResponse(
    val page: Int,
    val results: List<MovieApiResponse>,
    val totalPages: Int,
    val totalResults: Int
)
@Serializable
data class MovieDetailResponse(
    val id: Int,
    val backdropPath: String?,
    val budget: Int,
    val genres: List<GenreDto>,
    val releaseDate: String,
    val overview: String,
    val title: String,
    val productionCompanies: List<ProductionCompanyDto>

)
@Serializable
data class GenreDto(
    val id: Int,
    val name: String
)
@Serializable
data class ProductionCompanyDto(
    val id: Int,
    val logoPath: String?,
    val name: String
)
