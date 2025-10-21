package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mason.cinesync.repository.AuthRepository
import com.mason.cinesync.retrofit.RetrofitInstance
import com.mason.cinesync.service.UsersService
import com.mason.cinesync.token.TokenManager

class AuthViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            val authService = RetrofitInstance.createService<UsersService>()
            val authRepository = AuthRepository(authService, TokenManager)
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}