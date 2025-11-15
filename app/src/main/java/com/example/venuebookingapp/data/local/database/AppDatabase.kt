package com.example.venuebookingapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.venuebookingapp.data.local.dao.BookingDao
import com.example.venuebookingapp.data.local.dao.ReviewDao
import com.example.venuebookingapp.data.local.dao.UserDao
import com.example.venuebookingapp.data.local.dao.VenueDao
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.data.local.entity.Review
import com.example.venuebookingapp.data.local.entity.User
import com.example.venuebookingapp.data.local.entity.Venue

@Database(
    entities = [
        User::class,
        Venue::class,
        Booking::class,
        Review::class
    ],
    version = 1, // Increment this number if you change the schema
    exportSchema = false // Not needed for this project
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun venueDao(): VenueDao

    abstract fun bookingDao(): BookingDao

    abstract fun reviewDao(): ReviewDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's not null
            return INSTANCE ?: synchronized(this) {
                // If instance is null, create a new one
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "venue_booking_database" // Name of the database file
                )
                    .fallbackToDestructiveMigration() // Strategy for schema changes (simple for a project)
                    .build()

                // Assign the new instance to INSTANCE
                INSTANCE = instance

                // Return the new instance
                instance
            }
        }
    }
}