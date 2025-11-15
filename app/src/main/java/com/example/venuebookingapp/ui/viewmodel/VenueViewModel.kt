package com.example.venuebookingapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.venuebookingapp.data.local.entity.Venue
import com.example.venuebookingapp.data.repository.VenueRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VenueViewModel(private val venueRepository: VenueRepository) : ViewModel() {

    private val _venues = MutableStateFlow<List<Venue>>(emptyList())
    val venues: StateFlow<List<Venue>> = _venues

    private val _selectedVenue = MutableStateFlow<Venue?>(null)
    val selectedVenue: StateFlow<Venue?> = _selectedVenue

    private val _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState: StateFlow<OperationState> = _operationState

    fun loadAllVenues() {
        viewModelScope.launch {
            venueRepository.getAllApprovedVenues().collect { venueList ->
                _venues.value = venueList
            }
        }
    }

    fun loadVenuesByOwner(ownerId: Int) {
        viewModelScope.launch {
            venueRepository.getVenuesByOwner(ownerId).collect { venueList ->
                _venues.value = venueList
            }
        }
    }

    fun loadVenueById(venueId: Int) {
        viewModelScope.launch {
            venueRepository.getVenueById(venueId).collect { venue ->
                _selectedVenue.value = venue
            }
        }
    }

    fun addVenue(venue: Venue) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                venueRepository.insert(venue)
                _operationState.value = OperationState.Success("Venue added successfully")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to add venue")
            }
        }
    }

    fun updateVenue(venue: Venue) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                venueRepository.update(venue)
                _operationState.value = OperationState.Success("Venue updated successfully")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to update venue")
            }
        }
    }

    fun deleteVenue(venue: Venue) {
        viewModelScope.launch {
            _operationState.value = OperationState.Loading
            try {
                venueRepository.delete(venue)
                _operationState.value = OperationState.Success("Venue deleted successfully")
            } catch (e: Exception) {
                _operationState.value = OperationState.Error(e.message ?: "Failed to delete venue")
            }
        }
    }
}


class VenueViewModelFactory(private val repository: VenueRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VenueViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VenueViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}