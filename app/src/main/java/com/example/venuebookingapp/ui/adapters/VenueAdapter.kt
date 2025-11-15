package com.example.venuebookingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.venuebookingapp.data.local.entity.Venue
import com.example.venuebookingapp.databinding.ItemVenueBinding

class VenueAdapter(
    private val userRole: String,
    private val onVenueClick: (Venue) -> Unit,
    private val onEditClick: (Venue) -> Unit,   // 1. ADDED THIS
    private val onDeleteClick: (Venue) -> Unit // 2. ADDED THIS
) : ListAdapter<Venue, VenueAdapter.VenueViewHolder>(VenueDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val binding = ItemVenueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VenueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VenueViewHolder(
        private val binding: ItemVenueBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(venue: Venue) {
            binding.apply {
                // Your existing code
                tvVenueName.text = venue.name
                tvLocation.text = venue.location
                tvPrice.text = "KES ${venue.pricePerDay}/day"
                tvCapacity.text = "Capacity: ${venue.capacity}"
                tvAmenities.text = venue.amenities

                // Click listener for the whole item
                root.setOnClickListener {
                    onVenueClick(venue)
                }

                // --- 3. ADDED THIS BLOCK ---
                // Show/hide owner controls based on role
                if (userRole == "VENUE_OWNER") {
                    layoutOwnerControls.isVisible = true
                } else {
                    layoutOwnerControls.isVisible = false
                }

                // Set click listeners for the buttons
                btnEdit.setOnClickListener {
                    onEditClick(venue)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(venue)
                }
                // --- END OF ADDED BLOCK ---
            }
        }
    }

    class VenueDiffCallback : DiffUtil.ItemCallback<Venue>() {
        override fun areItemsTheSame(oldItem: Venue, newItem: Venue): Boolean {
            return oldItem.venueId == newItem.venueId
        }

        override fun areContentsTheSame(oldItem: Venue, newItem: Venue): Boolean {
            return oldItem == newItem
        }
    }
}