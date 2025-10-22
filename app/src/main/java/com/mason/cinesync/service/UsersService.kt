package com.mason.cinesync.service

import com.mason.cinesync.model.dto.UsersDto
import retrofit2.http.GET

interface UsersService {

    @GET("/api/users/me")
    suspend fun getLoggedInUser(): UsersDto
}