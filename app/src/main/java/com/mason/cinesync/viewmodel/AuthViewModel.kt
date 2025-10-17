package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.cinesync.model.dto.LoginResponseDto
import com.mason.cinesync.model.dto.UserLoginDto
import com.mason.cinesync.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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


    fun login(request: UserLoginDto) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = authRepository.login(request)
                _uiState.value = AuthUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }


}