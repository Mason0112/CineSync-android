package com.mason.cinesync.repository

import com.mason.cinesync.service.UsersService

class UsersRepository(
    private val usersService: UsersService
) {
    suspend fun getLoggedInUser() = usersService.getLoggedInUser()

}