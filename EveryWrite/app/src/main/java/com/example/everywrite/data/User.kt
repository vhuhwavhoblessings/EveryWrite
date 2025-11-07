package com.example.everywrite.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val username: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)