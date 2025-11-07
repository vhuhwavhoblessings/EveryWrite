package com.example.everywrite.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val tags: String = "",
    val color: Int = 0,
    val weather: String? = null,
    val location: String? = null,
    val weatherIcon: String? = null,
    val imageUrl: String? = null
)