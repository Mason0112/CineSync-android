package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.cinesync.model.dto.UsersDto
import com.mason.cinesync.repository.AuthRepository
import com.mason.cinesync.repository.UsersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UsersUiState {
    object Idle : UsersUiState()
    object Loading : UsersUiState()
    data class Success(val loginResponse: UsersDto) : UsersUiState()
    data class Error(val message: String) : UsersUiState()
}


class UsersViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val _userUiState = MutableStateFlow<UsersUiState>(UsersUiState.Idle)
    val userUiState: StateFlow<UsersUiState> = _userUiState.asStateFlow()

    fun loadLoggedInUser() {
        _userUiState.value = UsersUiState.Loading
        viewModelScope.launch {
            try {
                val user = usersRepository.getLoggedInUser()
                _userUiState.value = UsersUiState.Success(user)
            } catch (e: Exception) {
                _userUiState.value = UsersUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun retry() {
        loadLoggedInUser()
    }

}