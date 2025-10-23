package com.mason.cinesync.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mason.cinesync.repository.UsersRepository
import com.mason.cinesync.retrofit.RetrofitInstance
import com.mason.cinesync.service.UsersService

class UsersViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            val usersService = RetrofitInstance.createService<UsersService>()
            val usersRepo = UsersRepository(usersService)
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(usersRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}