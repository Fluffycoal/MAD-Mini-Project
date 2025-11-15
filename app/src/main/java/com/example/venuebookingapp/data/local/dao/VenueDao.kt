package com.example.venuebookingapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.venuebookingapp.data.local.entity.Venue
import kotlinx.coroutines.flow.Flow


@Dao
interface VenueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVenue(venue: Venue)

    @Update
    suspend fun updateVenue(venue: Venue)

    @Delete
    suspend fun deleteVenue(venue: Venue)

    @Query("SELECT * FROM venues ORDER BY name ASC")
    fun getAllVenues(): Flow<List<Venue>>

    @Query("SELECT * FROM venues WHERE venueId = :venueId")
    fun getVenueById(venueId: Int): Flow<Venue>


    @Query("SELECT * FROM venues WHERE ownerId = :ownerId ORDER BY name ASC")
    fun getVenuesByOwner(ownerId: Int): Flow<List<Venue>>

    @Query("SELECT * FROM venues WHERE status = 'APPROVED' ORDER BY name ASC")
    fun getAllApprovedVenues(): Flow<List<Venue>>
}