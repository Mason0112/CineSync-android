package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import com.mason.cinesync.model.dto.LoginResponseDto
import com.mason.cinesync.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val loginResponse: LoginResponseDto) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel (private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    // Publicly exposed StateFlow for the UI to observe
    val uiState: StateFlow<AuthUiState> = _uiState


}