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
import com.example.venuebookingapp.databinding.FragmentBookingBinding
import com.example.venuebookingapp.ui.adapters.BookingAdapter
import com.example.venuebookingapp.ui.viewmodel.BookingViewModel
import com.example.venuebookingapp.ui.viewmodel.BookingViewModelFactory
import com.example.venuebookingapp.ui.viewmodel.OperationState
import kotlinx.coroutines.launch


class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    // Get the ViewModel using the Factory
    private val bookingViewModel: BookingViewModel by viewModels {
        BookingViewModelFactory((requireActivity().application as VenueBookingApplication).bookingRepository)
    }

    private lateinit var bookingAdapter: BookingAdapter

    // User data passed from MainActivity
    private var userId: Int = -1
    private lateinit var userRole: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("USER_ID")
            userRole = it.getString("USER_ROLE") ?: "CLIENT"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Load the correct list of bookings based on user role
        if (userRole == "CLIENT") {
            bookingViewModel.loadClientBookings(userId)
        } else {
            // "VENUE_OWNER"
            bookingViewModel.loadOwnerBookings(userId)
        }
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter { booking ->
            // Handle click on a booking
            Toast.makeText(context, "Clicked on booking ${booking.bookingId}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewBookings.apply {
            adapter = bookingAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        // ✅ CORRECTED: Observes 'bookingViewModel.bookings' directly
        viewLifecycleOwner.lifecycleScope.launch {
            bookingViewModel.bookings.collect { bookings ->
                if (bookings.isEmpty()) {
                    binding.tvNoBookings.isVisible = true
                    binding.recyclerViewBookings.isVisible = false
                } else {
                    binding.tvNoBookings.isVisible = false
                    binding.recyclerViewBookings.isVisible = true
                    bookingAdapter.submitList(bookings)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // Observes the shared 'OperationState'
            bookingViewModel.operationState.collect { state ->
                when (state) {
                    is OperationState.Success -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is OperationState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {} // Do nothing on Idle or Loading
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding
    }
}