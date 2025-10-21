package com.mason.cinesync.model.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CommentDto(
    val id: Long,
    val movieId: String,
    val userId: Long,
    val userName: String = "",
    val content: String,
    @Contextual val createdAt: LocalDateTime?,
    @Contextual val updatedAt: LocalDateTime?,
)

@Serializable
data class CommentRequest(
    val movieId: String,
    val content: String
)