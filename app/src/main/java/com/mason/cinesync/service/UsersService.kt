package com.mason.cinesync.service

import com.mason.cinesync.model.dto.LoginResponseDto
import com.mason.cinesync.model.dto.UserLoginDto
import com.mason.cinesync.model.dto.UserRegisterDto
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersService {

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: UserLoginDto): LoginResponseDto

    @POST("/api/auth/register")
    suspend fun register(@Body registerRequest: UserRegisterDto): LoginResponseDto
}