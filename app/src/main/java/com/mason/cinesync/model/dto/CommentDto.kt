package com.mason.cinesync.model.dto

import java.time.LocalDateTime

data class CommentDto(
    val id: Long,
    val movieId: String,
    val userId: Long,
    val userName: String = "",
    val content: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)

data class CommentRequest(
    val movieId: String,
    val content: String
)