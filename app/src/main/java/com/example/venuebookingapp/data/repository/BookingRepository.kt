package com.example.venuebookingapp.data.repository

import com.example.venuebookingapp.data.local.dao.BookingDao
import com.example.venuebookingapp.data.local.entity.Booking
import kotlinx.coroutines.flow.Flow

class BookingRepository(private val bookingDao: BookingDao) {

    suspend fun createBooking(booking: Booking) {
        bookingDao.insertBooking(booking)
    }

    fun getBookingsByClient(clientId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingsByClientId(clientId)
    }

    fun getBookingsForVenue(venueId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingsForVenue(venueId)
    }

    suspend fun deleteBooking(bookingId: Int) {
        bookingDao.deleteBooking(bookingId)
    }
    fun getBookingsForOwner(ownerId: Int): Flow<List<Booking>> {
        return bookingDao.getBookingsForOwner(ownerId)
    }

    suspend fun updateBookingStatus(bookingId: Int, status: String) {
        bookingDao.updateBookingStatus(bookingId, status)
    }

    suspend fun markAsPaid(bookingId: Int) {
        bookingDao.markAsPaid(bookingId)
    }
}