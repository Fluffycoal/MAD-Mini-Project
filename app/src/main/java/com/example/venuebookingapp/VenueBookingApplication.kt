package com.example.venuebookingapp

import android.app.Application
import com.example.venuebookingapp.data.local.database.AppDatabase
import com.example.venuebookingapp.data.repository.BookingRepository
import com.example.venuebookingapp.data.repository.ReviewRepository
import com.example.venuebookingapp.data.repository.UserRepository
import com.example.venuebookingapp.data.repository.VenueRepository


class VenueBookingApplication : Application() {

    // Use 'lazy' to create the database instance only when it's first needed
    private val database by lazy { AppDatabase.getDatabase(this) }

    // Create lazy instances of all your repositories
    val userRepository by lazy { UserRepository(database.userDao()) }
    val venueRepository by lazy { VenueRepository(database.venueDao()) }
    val bookingRepository by lazy { BookingRepository(database.bookingDao()) }
    val reviewRepository by lazy { ReviewRepository(database.reviewDao()) }
}