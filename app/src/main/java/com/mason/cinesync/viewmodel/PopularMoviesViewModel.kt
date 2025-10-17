package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.cinesync.model.Result
import com.mason.cinesync.model.dto.MovieApiResponse
import com.mason.cinesync.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PopularMoviesUiState {
    object Loading : PopularMoviesUiState()
    data class Success(
        val movies: List<MovieApiResponse>,
        val currentPage: Int,
        val totalPages: Int,
        val isLoadingMore: Boolean = false
    ) : PopularMoviesUiState()
    data class Error(val message: String, val movies: List<MovieApiResponse> = emptyList()) : PopularMoviesUiState()
}

class PopularMoviesViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<PopularMoviesUiState>(PopularMoviesUiState.Loading)
    val uiState: StateFlow<PopularMoviesUiState> = _uiState.asStateFlow()

    init {
        // Fetch the first page when the ViewModel is created
        loadNextPage()
    }

    fun loadNextPage() {
        val currentState = _uiState.value

        // 防止重複載入
        if (currentState is PopularMoviesUiState.Success &&
            (currentState.isLoadingMore || currentState.currentPage >= currentState.totalPages)) {
            return
        }

        // 如果已經有資料,設置 isLoadingMore
        if (currentState is PopularMoviesUiState.Success) {
            _uiState.value = currentState.copy(isLoadingMore = true)
        } else {
            _uiState.value = PopularMoviesUiState.Loading
        }

        viewModelScope.launch {
            // 計算下一頁的頁碼
            val nextPage = if (currentState is PopularMoviesUiState.Success) {
                currentState.currentPage + 1
            } else {
                1
            }

            movieRepository.getPopularMoviesStream(nextPage, "en-US")
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Already handled above
                        }
                        is Result.Success -> {
                            // 合併新舊資料
                            val currentMovies = if (_uiState.value is PopularMoviesUiState.Success) {
                                (_uiState.value as PopularMoviesUiState.Success).movies
                            } else {
                                emptyList()
                            }

                            val updatedMovies = (currentMovies + result.data.results).distinctBy { it.id }

                            _uiState.value = PopularMoviesUiState.Success(
                                movies = updatedMovies,
                                currentPage = result.data.page,
                                totalPages = result.data.totalPages,
                                isLoadingMore = false
                            )
                        }
                        is Result.Error -> {
                            // 保留已載入的資料
                            val currentMovies = if (_uiState.value is PopularMoviesUiState.Success) {
                                (_uiState.value as PopularMoviesUiState.Success).movies
                            } else {
                                emptyList()
                            }

                            _uiState.value = PopularMoviesUiState.Error(
                                message = result.exception.message ?: "Unknown error",
                                movies = currentMovies
                            )
                        }
                    }
                }
        }
    }

    fun retry() {
        loadNextPage()
    }
}
