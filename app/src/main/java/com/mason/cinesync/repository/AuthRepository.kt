package com.mason.cinesync.repository

import android.util.Log
import com.mason.cinesync.model.Result
import com.mason.cinesync.model.dto.LoginResponseDto
import com.mason.cinesync.model.dto.UserLoginDto
import com.mason.cinesync.model.dto.UserRegisterDto
import com.mason.cinesync.service.UsersService
import com.mason.cinesync.token.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val authApiService: UsersService,
    private val tokenManager: TokenManager
) {
    private val TAG = AuthRepository::class.java.simpleName

    fun login(request: UserLoginDto): Flow<Result<LoginResponseDto>> = flow {
        emit(Result.Loading)
        try {
            val response = authApiService.login(request)
            tokenManager.saveToken(response.token)
            Log.d(TAG, "login success: ${response.users.userName}")
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "login failed: ${e.message}", e)
            emit(Result.Error(e))
        }
    }

    fun register(request: UserRegisterDto): Flow<Result<LoginResponseDto>> = flow {
        emit(Result.Loading)
        try {
            val response = authApiService.register(request)
            tokenManager.saveToken(response.token)
            Log.d(TAG, "register success: ${response.users.userName}")
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "register failed: ${e.message}", e)
            emit(Result.Error(e))
        }
    }

    fun logout() {
        tokenManager.deleteToken()
        Log.d(TAG, "user logged out")
    }

    fun isLoggedIn(): Boolean {
        return tokenManager.getToken() != null
    }
}