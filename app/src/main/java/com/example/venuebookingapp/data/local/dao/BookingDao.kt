package com.example.venuebookingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.venuebookingapp.data.local.entity.Booking
import kotlinx.coroutines.flow.Flow


@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings WHERE clientId = :clientId ORDER BY BookingDate DESC")
    fun getBookingsByClientId(clientId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM bookings WHERE venueId = :venueId ORDER BY BookingDate ASC")
    fun getBookingsForVenue(venueId: Int): Flow<List<Booking>>

    @Query("DELETE FROM bookings WHERE bookingId = :bookingId")
    suspend fun deleteBooking(bookingId: Int)

    @Query("""
        SELECT bookings.* FROM bookings
        INNER JOIN venues ON bookings.venueId = venues.venueId
        WHERE venues.ownerId = :ownerId
        ORDER BY bookings.bookingDate ASC
    """)
    fun getBookingsForOwner(ownerId: Int): Flow<List<Booking>>

    @Query("UPDATE bookings SET status = :status WHERE bookingId = :bookingId")
    suspend fun updateBookingStatus(bookingId: Int, status: String)

    @Query("UPDATE bookings SET isPaid = 1 WHERE bookingId = :bookingId")
    suspend fun markAsPaid(bookingId: Int)
}