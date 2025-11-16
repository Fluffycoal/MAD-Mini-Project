package com.example.venuebookingapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.venuebookingapp.R
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.data.local.entity.Venue
import com.example.venuebookingapp.databinding.FragmentVenueBinding
import com.example.venuebookingapp.ui.adapters.VenueAdapter
import com.example.venuebookingapp.ui.dialogs.AddVenueDialog
import com.example.venuebookingapp.ui.dialogs.EditVenueDialog
import com.example.venuebookingapp.ui.viewmodel.OperationState
import com.example.venuebookingapp.ui.viewmodel.VenueViewModel
import com.example.venuebookingapp.ui.viewmodel.VenueViewModelFactory
import kotlinx.coroutines.launch
import com.example.venuebookingapp.ui.fragments.VenueDetailFragment

class VenuesFragment : Fragment() {

    private var _binding: FragmentVenueBinding? = null
    private val binding get() = _binding!!

    private lateinit var venueAdapter: VenueAdapter
    private var userId: Int = 0
    private var userRole: String = "CLIENT"

    private val venueViewModel: VenueViewModel by viewModels {
        VenueViewModelFactory(
            (requireActivity().application as VenueBookingApplication).venueRepository
        )
    }

    companion object {
        private const val ARG_USER_ID = "USER_ID"
        private const val ARG_USER_ROLE = "USER_ROLE"

        fun newInstance(userId: Int, userRole: String) = VenuesFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_USER_ID, userId)
                putString(ARG_USER_ROLE, userRole)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt(ARG_USER_ID)
            userRole = it.getString(ARG_USER_ROLE) ?: "CLIENT"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVenueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        observeVenues()
        observeOperationState()
        loadVenues()
    }

    private fun observeOperationState() {
        lifecycleScope.launch {
            venueViewModel.operationState.collect { state ->
                when (state) {
                    is OperationState.Success -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        loadVenues()
                    }
                    is OperationState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerView() {
        venueAdapter = VenueAdapter(
            userRole = userRole,
            onVenueClick = { venue ->
                if (userRole == "CLIENT") {
                    val fragment = VenueDetailFragment.newInstance(venue.venueId, userId, userRole)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(context, "Use the action buttons below.", Toast.LENGTH_SHORT).show()
                }
            },
            onEditClick = { venue ->
                showEditVenueDialog(venue)
            },
            onDeleteClick = { venue ->
                deleteVenue(venue)
            },
            onViewReviewsClick = { venue -> // Reviews Button Handler
                val fragment = VenueDetailFragment.newInstance(venue.venueId, userId, userRole)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        )

        binding.recyclerViewVenues.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = venueAdapter
        }
    }

    private fun showEditVenueDialog(venue: Venue) {
        val dialog = EditVenueDialog.newInstance(venue)
        dialog.setOnVenueUpdatedListener { updatedVenue ->
            venueViewModel.updateVenue(updatedVenue)
        }
        dialog.show(childFragmentManager, "EditVenueDialog")
    }

    private fun deleteVenue(venue: Venue) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Venue")
            .setMessage("Are you sure you want to delete ${venue.name}?")
            .setPositiveButton("Delete") { _, _ ->
                venueViewModel.deleteVenue(venue)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Manage Search Bar visibility based on userRole
    private fun setupFab() {
        if (userRole == "VENUE_OWNER") {
            binding.fabAddVenue.isVisible = true
            binding.fabAddVenue.setOnClickListener {
                showAddVenueDialog()
            }
            binding.layoutSearchBar.visibility = View.GONE

        } else {
            binding.fabAddVenue.isVisible = false
            // Show the Search Bar for the Client
            binding.layoutSearchBar.visibility = View.VISIBLE
        }
    }

    private fun showAddVenueDialog() {
        val dialog = AddVenueDialog.newInstance(userId)
        dialog.setOnVenueAddedListener { venue ->
            venueViewModel.addVenue(venue)
        }
        dialog.show(childFragmentManager, "AddVenueDialog")
    }

    private fun observeVenues() {
        lifecycleScope.launch {
            venueViewModel.venues.collect { venues ->
                if (venues.isEmpty()) {

                    binding.tvNoVenues.isVisible = true
                    binding.recyclerViewVenues.isVisible = false
                } else {

                    binding.tvNoVenues.isVisible = false
                    binding.recyclerViewVenues.isVisible = true
                    venueAdapter.submitList(venues)
                }
            }
        }
    }

    private fun loadVenues() {
        if (userRole == "VENUE_OWNER") {
            venueViewModel.loadVenuesByOwner(userId)
        } else {
            venueViewModel.loadAllVenues()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}