package com.mason.cinesync.service

import com.mason.cinesync.model.dto.LoginResponseDto
import com.mason.cinesync.model.dto.UserLoginDto
import com.mason.cinesync.model.dto.UserRegisterDto
import com.mason.cinesync.model.dto.UsersDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: UserLoginDto): LoginResponseDto

    @POST("/api/auth/register")
    suspend fun register(@Body registerRequest: UserRegisterDto): LoginResponseDto

}