package com.mason.cinesync.model.dto

import com.mason.cinesync.model.enum.UsersRole

data class UserRegisterDto(
    val email: String,
    val userName: String,
    val password: String
)

data class UserLoginDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val token: String?,
    val users: UsersDto
)

data class UsersDto(
    val id: Long,
    val userName: String,
    val email: String,
    val usersRole: UsersRole
)