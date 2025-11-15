package com.example.venuebookingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.data.repository.BookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookingViewModel(private val bookingRepository: BookingRepository) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    fun loadClientBookings(clientId: Int) {
        viewModelScope.launch {
            bookingRepository.getBookingsByClient(clientId).collect { bookingList ->
                _bookings.value = bookingList
            }
        }
    }

    fun loadOwnerBookings(ownerId: Int) {
        viewModelScope.launch {
            bookingRepository.getBookingsForOwner(ownerId).collect { bookingList ->
                _bookings.value = bookingList
            }
        }
    }

    fun createBooking(booking: Booking) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                bookingRepository.createBooking(booking)
                _operationState.value = OperationState.Success("Booking created successfully")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to create booking")
            }
        }
    }

    fun approveBooking(bookingId: Int) {
        viewModelScope.launch {
            try {
                bookingRepository.updateBookingStatus(bookingId, "APPROVED")
                _operationState.value = OperationState.Success("Booking approved")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to approve")
            }
        }
    }

    fun rejectBooking(bookingId: Int) {
        viewModelScope.launch {
            try {
                bookingRepository.updateBookingStatus(bookingId, "REJECTED")
                _operationState.value = OperationState.Success("Booking rejected")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to reject")
            }
        }
    }

    fun markAsPaid(bookingId: Int) {
        viewModelScope.launch {
            try {
                bookingRepository.markAsPaid(bookingId)
                _operationState.value = OperationState.Success("Marked as paid")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to mark as paid")
            }
        }
    }
}

class BookingViewModelFactory(private val repository: BookingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}