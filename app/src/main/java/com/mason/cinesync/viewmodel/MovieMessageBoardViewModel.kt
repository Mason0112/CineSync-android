package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mason.cinesync.model.dto.CommentDto
import com.mason.cinesync.model.dto.CommentRequest
import com.mason.cinesync.model.dto.MovieDetailResponse
import com.mason.cinesync.repository.CommentRepository
import com.mason.cinesync.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MovieMessageBoardUiState {
    object Loading : MovieMessageBoardUiState()
    data class Success(
        val movieDetail: MovieDetailResponse,
        val comments: List<CommentDto>,
        val currentPage: Int,
        val hasMoreComments: Boolean
    ) : MovieMessageBoardUiState()

    data class Error(val message: String) : MovieMessageBoardUiState()
}

sealed class CreateCommentUiState {
    object Idle : CreateCommentUiState()
    object Loading : CreateCommentUiState()
    data class Success(val comment: CommentDto) : CreateCommentUiState()
    data class Error(val message: String) : CreateCommentUiState()
}


class MovieMessageBoardViewModel(
    private val movieId: Int,
    private val movieRepository: MovieRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<MovieMessageBoardUiState>(MovieMessageBoardUiState.Loading)
    val uiState: StateFlow<MovieMessageBoardUiState> = _uiState.asStateFlow()

    private val _commentUiState =
        MutableStateFlow<CreateCommentUiState>(CreateCommentUiState.Idle)
    val commentUiState: StateFlow<CreateCommentUiState> = _commentUiState.asStateFlow()

    private var isLoadingMore = false
    private val pageSize = 5

    init {
        loadMovieAndComments()
    }

    fun createComment(commentRequest: CommentRequest) {
        viewModelScope.launch {
            _commentUiState.value = CreateCommentUiState.Loading
            try {
                val createdComment = commentRepository.createComment(commentRequest)
                _commentUiState.value = CreateCommentUiState.Success(createdComment)
                // Refresh comments to show the new one
                refreshComments()
            } catch (e: Exception) {
                _commentUiState.value = CreateCommentUiState.Error(
                    e.message ?: "Failed to create comment"
                )
            }
        }
    }


    private fun loadMovieAndComments(page: Int = 0) {
        viewModelScope.launch {
            try {
                // Load movie details and first page of comments in parallel
                val movieDetailDeferred =
                    async { movieRepository.getMovieDetails(movieId, "en-US") }
                val commentsDeferred =
                    async { commentRepository.getComments(movieId.toString(), page, pageSize) }

                val movieDetail = movieDetailDeferred.await()
                val commentsPage = commentsDeferred.await()

                _uiState.value = MovieMessageBoardUiState.Success(
                    movieDetail = movieDetail,
                    comments = commentsPage.content,
                    currentPage = page,
                    hasMoreComments = !commentsPage.last
                )
            } catch (e: Exception) {
                _uiState.value = MovieMessageBoardUiState.Error(
                    message = e.message ?: "Failed to load data"
                )
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState !is MovieMessageBoardUiState.Success || isLoadingMore) return
        if (!currentState.hasMoreComments) return

        isLoadingMore = true
        viewModelScope.launch {
            try {
                val nextPage = currentState.currentPage + 1
                val commentsPage =
                    commentRepository.getComments(movieId.toString(), nextPage, pageSize)

                _uiState.value = currentState.copy(
                    comments = (currentState.comments + commentsPage.content).distinctBy { it.id },
                    currentPage = nextPage,
                    hasMoreComments = !commentsPage.last
                )
            } catch (e: Exception) {
                // Keep existing state on error
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun refreshComments() {
        _uiState.value = MovieMessageBoardUiState.Loading
        loadMovieAndComments()
    }
}