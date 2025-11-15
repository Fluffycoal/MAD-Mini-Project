package com.example.venuebookingapp.data.repository

import com.example.venuebookingapp.data.local.dao.VenueDao
import com.example.venuebookingapp.data.local.entity.Venue
import kotlinx.coroutines.flow.Flow

class VenueRepository(private val venueDao: VenueDao) {

    val allVenues: Flow<List<Venue>> = venueDao.getAllVenues()


    suspend fun insert(venue: Venue) {
        venueDao.insertVenue(venue)
    }

    suspend fun update(venue: Venue) {
        venueDao.updateVenue(venue)
    }

    suspend fun delete(venue: Venue) {
        venueDao.deleteVenue(venue)
    }

    fun getVenuesByOwner(ownerId: Int): Flow<List<Venue>> {
        return venueDao.getVenuesByOwner(ownerId)
    }

    fun getVenueById(venueId: Int): Flow<Venue> {
        return venueDao.getVenueById(venueId)
    }

    fun getAllApprovedVenues(): Flow<List<Venue>> {
        return venueDao.getAllApprovedVenues()
    }
}