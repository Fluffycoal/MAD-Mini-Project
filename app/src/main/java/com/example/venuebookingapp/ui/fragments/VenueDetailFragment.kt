package com.example.venuebookingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.data.local.entity.Venue
import com.example.venuebookingapp.databinding.FragmentVenueDetailBinding
import com.example.venuebookingapp.ui.viewmodel.BookingViewModel
import com.example.venuebookingapp.ui.viewmodel.BookingViewModelFactory
import com.example.venuebookingapp.ui.viewmodel.OperationState
import com.example.venuebookingapp.ui.viewmodel.VenueViewModel
import com.example.venuebookingapp.ui.viewmodel.VenueViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VenueDetailFragment : Fragment() {

    private var _binding: FragmentVenueDetailBinding? = null
    private val binding get() = _binding!!

    // ViewModels for reading venue data and creating a booking
    private val venueViewModel: VenueViewModel by viewModels {
        VenueViewModelFactory((requireActivity().application as VenueBookingApplication).venueRepository)
    }
    private val bookingViewModel: BookingViewModel by viewModels {
        BookingViewModelFactory((requireActivity().application as VenueBookingApplication).bookingRepository)
    }

    private var venueId: Int = -1
    private var clientId: Int = -1
    private lateinit var userRole: String
    private var currentVenue: Venue? = null // To hold the fetched venue data

    companion object {
        private const val ARG_VENUE_ID = "venue_id"
        private const val ARG_CLIENT_ID = "client_id"
        private const val ARG_USER_ROLE = "user_role"

        fun newInstance(venueId: Int, clientId: Int, userRole: String) = VenueDetailFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_VENUE_ID, venueId)
                putInt(ARG_CLIENT_ID, clientId)
                putString(ARG_USER_ROLE, userRole)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            venueId = it.getInt(ARG_VENUE_ID)
            clientId = it.getInt(ARG_CLIENT_ID)
            userRole = it.getString(ARG_USER_ROLE) ?: "CLIENT"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVenueDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Load Venue Data
        observeVenueDetails()

        // 2. Setup Booking Button
        setupBookingButton()

        // 3. Observe Booking Status (to show success/error)
        observeBookingOperation()

        // Trigger data load
        venueViewModel.loadVenueById(venueId)
    }

    private fun observeVenueDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            // VenueViewModel needs a function to load a single venue by ID and return a Flow
            venueViewModel.selectedVenue.collect { venue ->
                venue?.let {
                    currentVenue = it
                    // Update UI fields
                    binding.tvDetailName.text = it.name
                    binding.tvDetailLocation.text = "Location: ${it.location}"
                    binding.tvDetailCapacity.text = "Capacity: ${it.capacity}"
                    binding.tvDetailPrice.text = "KES ${it.pricePerDay} / Day"
                    binding.tvDetailAmenities.text = it.amenities

                    if (userRole != "CLIENT") {
                        binding.btnBookNow.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupBookingButton() {
        binding.btnBookNow.setOnClickListener {
            currentVenue?.let {
                val booking = Booking(
                    venueId = it.venueId,
                    clientId = clientId,
                    bookingDate = "2026-01-10",
                    startTime = "09:00",
                    endTime = "17:00",
                    totalAmount = it.pricePerDay,
                    createdAt = System.currentTimeMillis()
                )
                bookingViewModel.createBooking(booking)
            }
        }
    }

    private fun observeBookingOperation() {
        viewLifecycleOwner.lifecycleScope.launch {
            bookingViewModel.operationState.collect { state ->
                when (state) {
                    is OperationState.Success -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        // Optional: Navigate back to Venues list or My Bookings
                    }
                    is OperationState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}