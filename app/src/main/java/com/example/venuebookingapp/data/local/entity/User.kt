package com.example.venuebookingapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users",
    indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val role: String // "CLIENT" or "VENUE_OWNER"
)