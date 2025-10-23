package com.mason.cinesync.repository

import com.mason.cinesync.model.dto.CommentDto
import com.mason.cinesync.model.dto.CommentRequest
import com.mason.cinesync.model.dto.Page
import com.mason.cinesync.service.CommentService

class CommentRepository(
    private val commentService: CommentService
) {
    suspend fun createComment(commentRequest: CommentRequest) =
        commentService.createComment(commentRequest)


    suspend fun getComments(movieId: String, page: Int, size: Int): Page<CommentDto> =
        commentService.getComments(movieId, page, size)

}