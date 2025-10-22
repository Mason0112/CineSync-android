package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import com.mason.cinesync.model.dto.UsersDto
import com.mason.cinesync.repository.AuthRepository

sealed class UserUiState {
    object Idle : UserUiState()
    object Loading : UserUiState()
    data class Success(val loginResponse: UsersDto) : UserUiState()
    data class Error(val message: String) : UserUiState()
}


class UserViewModel (private val authRepository: AuthRepository) : ViewModel() {

    suspend fun logout() {
        authRepository.logout()
    }
}