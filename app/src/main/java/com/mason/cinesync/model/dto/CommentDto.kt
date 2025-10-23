package com.mason.cinesync.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val id: Long,
    val movieId: String,
    val userId: Long,
    val userName: String = "",
    val content: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class CommentRequest(
    val movieId: String,
    val content: String
)

@Serializable
data class Page<T>(
    val content: List<T>,
    val totalElements: Int,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)