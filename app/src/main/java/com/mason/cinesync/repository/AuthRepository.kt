package com.mason.cinesync.repository

import android.util.Log
import com.mason.cinesync.model.dto.LoginResponseDto
import com.mason.cinesync.model.dto.UserLoginDto
import com.mason.cinesync.model.dto.UserRegisterDto
import com.mason.cinesync.service.AuthService
import com.mason.cinesync.token.TokenManager

class AuthRepository(
    private val authApiService: AuthService,
    private val tokenManager: TokenManager
) {
    private val TAG = AuthRepository::class.java.simpleName

    suspend fun login(request: UserLoginDto): LoginResponseDto {
        val response = authApiService.login(request)
        tokenManager.saveToken(response.token)
        Log.d(TAG, "login success: ${response.users.userName}")
        return response
    }

    suspend fun register(request: UserRegisterDto): LoginResponseDto {
        val response = authApiService.register(request)
        tokenManager.saveToken(response.token)
        Log.d(TAG, "register success: ${response.users.userName}")
        return response
    }

    fun logout() {
        tokenManager.deleteToken()
        Log.d(TAG, "user logged out")
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}