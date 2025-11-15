package com.example.venuebookingapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "venues",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["ownerId"], // Links to the User who owns this venue
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ownerId"])] // Good practice for querying venues by owner
)
data class Venue(
    @PrimaryKey(autoGenerate = true)
    val venueId: Int = 0,
    val ownerId: Int, // Foreign key for the User (owner)
    val name: String,
    val location: String,
    val description: String,
    val capacity: Int,
    val pricePerDay: Double,
    val amenities: String, // e.g., "WiFi,Projector,Parking"
    val status: String = "PENDING",
    val imageUrl: String? = null
)