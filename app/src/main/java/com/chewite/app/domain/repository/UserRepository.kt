package com.chewite.app.domain.repository

import com.chewite.app.data.user.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val currentUser: StateFlow<User?>
    suspend fun loadUser()
    suspend fun updateUser(user: User)
    suspend fun clearUser()
}