package com.example.venuebookingapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "bookings",
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
data class Booking(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Int = 0,
    val venueId: Int,
    val clientId: Int,
    val bookingDate: String, // Format: "yyyy-MM-dd"
    val startTime: String, // Format: "HH:mm"
    val endTime: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val isPaid: Boolean = false,
    val totalAmount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
