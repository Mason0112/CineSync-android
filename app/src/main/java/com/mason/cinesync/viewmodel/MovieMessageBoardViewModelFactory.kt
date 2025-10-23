package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mason.cinesync.repository.CommentRepository
import com.mason.cinesync.repository.MovieRepository
import com.mason.cinesync.retrofit.RetrofitInstance
import com.mason.cinesync.service.CommentService
import com.mason.cinesync.service.MovieService

class MovieMessageBoardViewModelFactory(
    private val movieId: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieMessageBoardViewModel::class.java)) {
            val movieService = RetrofitInstance.createService<MovieService>()
            val commentService = RetrofitInstance.createService<CommentService>()
            val movieRepository = MovieRepository(movieService)
            val commentRepository = CommentRepository(commentService)

            @Suppress("UNCHECKED_CAST")
            return MovieMessageBoardViewModel(movieId, movieRepository, commentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
