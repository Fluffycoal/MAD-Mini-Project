package com.example.venuebookingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.venuebookingapp.data.local.entity.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews WHERE venueId = :venueId ORDER BY createdAt DESC")
    fun getReviewsForVenue(venueId: Int): Flow<List<Review>>
}