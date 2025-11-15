package com.example.venuebookingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Venue::class,
            parentColumns = ["venueId"],
            childColumns = ["venueId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Review(
    @PrimaryKey(autoGenerate = true)
    val reviewId: Int = 0,
    val venueId: Int,
    val clientId: Int,
    val rating: Float, // 1.0 to 5.0
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)