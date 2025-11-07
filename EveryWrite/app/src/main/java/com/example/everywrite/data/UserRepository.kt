package com.example.everywrite.data

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Boolean {
        return try {
            userDao.insertUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun isEmailTaken(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}