package com.example.venuebookingapp.data.repository

import com.example.venuebookingapp.data.local.dao.ReviewDao
import com.example.venuebookingapp.data.local.entity.Review
import kotlinx.coroutines.flow.Flow


class ReviewRepository(private val reviewDao: ReviewDao) {

    suspend fun addReview(review: Review) {
        reviewDao.insertReview(review)
    }

    fun getReviewsForVenue(venueId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsForVenue(venueId)
    }
}