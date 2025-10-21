package com.mason.cinesync.model.dto

import com.mason.cinesync.model.enum.UsersRole
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterDto(
    val email: String,
    val userName: String,
    val password: String
)

@Serializable
data class UserLoginDto(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponseDto(
    val token: String?,
    val users: UsersDto
)

@Serializable
data class UsersDto(
    val id: Long,
    val userName: String,
    val email: String,
    val usersRole: UsersRole
)