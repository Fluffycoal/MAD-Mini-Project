package com.example.venuebookingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.venuebookingapp.data.local.entity.Review
import com.example.venuebookingapp.data.repository.ReviewRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {

    // --- Review List State ---
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    // --- Add Review Operation State ---
    // Uses the shared OperationState class
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    fun loadReviewsForVenue(venueId: Int) {
        viewModelScope.launch {
            reviewRepository.getReviewsForVenue(venueId)
                .catch {
                    // You can add an error state flow here if needed
                    _reviews.value = emptyList()
                }
                .collect { reviewList ->
                    _reviews.value = reviewList
                }
        }
    }

    fun addReview(venueId: Int, clientId: Int, rating: Float, comment: String) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                val newReview = Review(
                    venueId = venueId,
                    clientId = clientId,
                    rating = rating,
                    comment = comment,
                    createdAt = System.currentTimeMillis()
                )
                reviewRepository.addReview(newReview)
                _operationState.value = OperationState.Success("Review added successfully")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add review")
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }
}

class ReviewViewModelFactory(private val repository: ReviewRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReviewViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}