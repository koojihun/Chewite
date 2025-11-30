package com.chewite.app.repository

import com.chewite.app.data.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser

    override suspend fun loadUser() {
        _currentUser.value = User(id = "test id", nickname = "test nickname")
    }

    override suspend fun updateUser(user: User) {
        _currentUser.value = user
    }

    override suspend fun clearUser() {
        _currentUser.value = null
    }
}