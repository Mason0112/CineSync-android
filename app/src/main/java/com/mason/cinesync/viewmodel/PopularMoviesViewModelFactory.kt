package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mason.cinesync.repository.MovieRepository
import com.mason.cinesync.retrofit.RetrofitInstance
import com.mason.cinesync.service.MovieService

class PopularMoviesViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularMoviesViewModel::class.java)) {
            val movieService = RetrofitInstance.create().create(MovieService::class.java)
            val movieRepository = MovieRepository(movieService)
            @Suppress("UNCHECKED_CAST")
            return PopularMoviesViewModel(movieRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
