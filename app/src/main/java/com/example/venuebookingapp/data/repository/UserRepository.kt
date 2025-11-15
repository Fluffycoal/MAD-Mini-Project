package com.example.venuebookingapp.data.repository

import com.example.venuebookingapp.data.local.dao.UserDao
import com.example.venuebookingapp.data.local.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun loginUser(email: String, password: String): User? {
        return userDao.loginUser(email, password)
    }

    suspend fun getUserByEmail(email: String): User? { // <-- THIS IS THE NEW FUNCTION
        return userDao.getUserByEmail(email)
    }

    fun getUserById(userId: Int): Flow<User> {
        return userDao.getUserById(userId)
    }
}