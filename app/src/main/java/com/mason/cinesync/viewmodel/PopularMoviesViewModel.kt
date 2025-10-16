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

class PopularMoviesViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _movies = MutableStateFlow<List<MovieApiResponse>>(emptyList())
    val movies: StateFlow<List<MovieApiResponse>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentPage = 0
    private var totalPages = 1
    private var isLoadingMore = false

    init {
        // Fetch the first page when the ViewModel is created
        loadNextPage()
    }

    fun loadNextPage() {
        // 防止重複載入
        if (isLoadingMore || currentPage >= totalPages) return

        isLoadingMore = true
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            movieRepository.getPopularMoviesStream(currentPage + 1, "en-US")
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Already handled by _isLoading
                        }
                        is Result.Success -> {
                            currentPage = result.data.page
                            totalPages = result.data.totalPages
                            // 將新的電影添加到現有列表，並過濾掉重複的 id
                            _movies.value = (_movies.value + result.data.results).distinctBy { it.id }
                            _isLoading.value = false
                            isLoadingMore = false
                        }
                        is Result.Error -> {
                            _error.value = result.exception.message
                            _isLoading.value = false
                            isLoadingMore = false
                        }
                    }
                }
        }
    }

    fun retry() {
        loadNextPage()
    }
}
