package com.example.venuebookingapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.venuebookingapp.data.local.dao.BookingDao
import com.example.venuebookingapp.data.local.dao.ReviewDao
import com.example.venuebookingapp.data.local.dao.UserDao
import com.example.venuebookingapp.data.local.dao.VenueDao
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.data.local.entity.Review
import com.example.venuebookingapp.data.local.entity.User
import com.example.venuebookingapp.data.local.entity.Venue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Venue::class,
        Booking::class,
        Review::class
    ],
    version = 1,
    exportSchema = false
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
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "venue_booking_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context)) // <-- ✅ ADD THIS LINE
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * ✅ ADD THIS WHOLE CLASS
     * This callback is triggered when the database is first created.
     */
    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Get a reference to the database instance
            INSTANCE?.let { database ->
                // Use a coroutine to add data in the background
                CoroutineScope(Dispatchers.IO).launch {
                    addTestUsers(database.userDao())
                }
            }
        }

        /**
         * Pre-populates the database with test accounts.
         */
        suspend fun addTestUsers(userDao: UserDao) {
            // Test Client Account
            val client = User(
                name = "Test Client",
                email = "client@test.com",
                phone = "0711111111",
                password = "123", // Use a simple password for testing
                role = "CLIENT"
            )
            userDao.insertUser(client)

            // Test Venue Owner Account
            val owner = User(
                name = "Test Owner",
                email = "owner@test.com",
                phone = "0722222222",
                password = "123",
                role = "VENUE_OWNER"
            )
            userDao.insertUser(owner)
        }
    }
}