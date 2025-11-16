package com.example.venuebookingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.databinding.ItemBookingBinding

class BookingAdapter(
    private val userRole: String, // Added: To check if Owner
    private val onBookingClick: (Booking) -> Unit,
    private val onApproveClick: (Booking) -> Unit, // Added: Approve action handler
    private val onRejectClick: (Booking) -> Unit  // Added: Reject action handler
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            // Bind data
            binding.tvVenueName.text = "Booking for Venue ID: ${booking.venueId}"
            binding.tvBookingDate.text = "Date: ${booking.bookingDate}"
            binding.tvBookingTime.text = "Time: ${booking.startTime} - ${booking.endTime}"
            binding.tvBookingStatus.text = booking.status

            // Set color based on status (your existing logic)
            when (booking.status) {
                "APPROVED" -> binding.tvBookingStatus.setBackgroundColor(0xFF4CAF50.toInt())
                "REJECTED" -> binding.tvBookingStatus.setBackgroundColor(0xFFF44336.toInt())
                else -> binding.tvBookingStatus.setBackgroundColor(0xFFFF9800.toInt())
            }

            // ✅ CRITICAL LOGIC: Show buttons only for Owner on PENDING bookings
            val showOwnerActions = userRole == "VENUE_OWNER" && booking.status == "PENDING"

            // This relies on you having the ID layoutOwnerActions in item_booking.xml
            binding.layoutOwnerActions.isVisible = showOwnerActions

            if (showOwnerActions) {
                // Set click listeners for the Owner buttons
                binding.btnApprove.setOnClickListener { onApproveClick(booking) }
                binding.btnReject.setOnClickListener { onRejectClick(booking) }
            }

            // Click listener for the whole card (for Client details or general info)
            binding.root.setOnClickListener {
                onBookingClick(booking)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
    override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem.bookingId == newItem.bookingId
    }

    override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem == newItem
    }
}