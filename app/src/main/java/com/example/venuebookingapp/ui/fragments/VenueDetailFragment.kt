package com.example.venuebookingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.data.local.entity.Venue
import com.example.venuebookingapp.databinding.FragmentVenueDetailBinding
import com.example.venuebookingapp.ui.adapters.ReviewAdapter // Ensure this import is correct
import com.example.venuebookingapp.ui.dialogs.AddReviewDialog // Ensure this import is correct
import com.example.venuebookingapp.ui.viewmodel.BookingViewModel
import com.example.venuebookingapp.ui.viewmodel.BookingViewModelFactory
import com.example.venuebookingapp.ui.viewmodel.OperationState
import com.example.venuebookingapp.ui.viewmodel.ReviewViewModel
import com.example.venuebookingapp.ui.viewmodel.ReviewViewModelFactory
import com.example.venuebookingapp.ui.viewmodel.VenueViewModel
import com.example.venuebookingapp.ui.viewmodel.VenueViewModelFactory
import kotlinx.coroutines.launch

class VenueDetailFragment : Fragment() {

    private var _binding: FragmentVenueDetailBinding? = null
    private val binding get() = _binding!!

    // ViewModels for data loading and transactions
    private val venueViewModel: VenueViewModel by viewModels {
        VenueViewModelFactory((requireActivity().application as VenueBookingApplication).venueRepository)
    }
    private val bookingViewModel: BookingViewModel by viewModels {
        BookingViewModelFactory((requireActivity().application as VenueBookingApplication).bookingRepository)
    }
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory((requireActivity().application as VenueBookingApplication).reviewRepository)
    }

    private lateinit var reviewAdapter: ReviewAdapter // Added Review Adapter

    private var venueId: Int = -1
    private var clientId: Int = -1
    private lateinit var userRole: String
    private var currentVenue: Venue? = null

    companion object {
        // NOTE: These keys must match the keys used in MainActivity and VenuesFragment
        private const val ARG_VENUE_ID = "VENUE_ID"
        private const val ARG_CLIENT_ID = "USER_ID"
        private const val ARG_USER_ROLE = "USER_ROLE"

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

        observeVenueDetails()
        setupBookingButton()
        setupReviewUI() // ✅ NEW: Setup Review List and Button
        observeBookingOperation()

        // Trigger data load
        venueViewModel.loadVenueById(venueId)
    }

    private fun observeVenueDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            venueViewModel.selectedVenue.collect { venue ->
                venue?.let {
                    currentVenue = it
                    // Update UI fields
                    binding.tvDetailName.text = it.name
                    binding.tvDetailLocation.text = "Location: ${it.location}"
                    binding.tvDetailCapacity.text = "Capacity: ${it.capacity}"
                    binding.tvDetailPrice.text = "KES ${it.pricePerDay} / Day"
                    binding.tvDetailAmenities.text = it.amenities

                    if (userRole == "CLIENT") {
                        binding.btnBookNow.visibility = View.VISIBLE
                        binding.btnLeaveReview.visibility = View.VISIBLE
                    } else {

                        binding.btnBookNow.visibility = View.GONE
                        binding.btnLeaveReview.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupBookingButton() {
        binding.btnBookNow.setOnClickListener {
            currentVenue?.let { venue ->
                // Hardcoded booking for quick demo
                val booking = Booking(
                    venueId = venue.venueId,
                    clientId = clientId,
                    bookingDate = "2026-01-10",
                    startTime = "09:00",
                    endTime = "17:00",
                    totalAmount = venue.pricePerDay,
                    createdAt = System.currentTimeMillis()
                )
                bookingViewModel.createBooking(booking)
            }
        }
    }

    private fun setupReviewUI() {
        // 1. Setup Adapter and RecyclerView
        reviewAdapter = ReviewAdapter()
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewAdapter
        }

        // 2. Load Reviews for this specific venue
        reviewViewModel.loadReviewsForVenue(venueId)

        // 3. Observe Review List
        viewLifecycleOwner.lifecycleScope.launch {
            reviewViewModel.reviews.collect { reviews ->
                if (reviews.isEmpty()) {
                    binding.tvNoReviews.visibility = View.VISIBLE
                    binding.rvReviews.visibility = View.GONE
                } else {
                    binding.tvNoReviews.visibility = View.GONE
                    binding.rvReviews.visibility = View.VISIBLE
                    reviewAdapter.submitList(reviews)
                }
            }
        }


        binding.btnLeaveReview.setOnClickListener {
            currentVenue?.let { venue ->
                val dialog = AddReviewDialog.newInstance(venue.venueId, clientId)
                dialog.show(childFragmentManager, "AddReviewDialog")
            }
        }
    }

    private fun observeBookingOperation() {
        viewLifecycleOwner.lifecycleScope.launch {
            bookingViewModel.operationState.collect { state ->
                when (state) {
                    is OperationState.Success -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        // Navigate back to the list screen after a successful booking
                        parentFragmentManager.popBackStack()
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