package com.mason.cinesync.service

import com.mason.cinesync.model.dto.CommentDto
import com.mason.cinesync.model.dto.CommentRequest
import com.mason.cinesync.model.dto.Page
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentService {


    @POST("/api/comments")
    suspend fun createComment(
        @Body commentRequest: CommentRequest
    ): CommentDto


    @GET("/api/comments/movie/{movieId}")
    suspend fun getComments(
        @Path("movieId") movieId: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") size: Int = 5
    ): Page<CommentDto>
}