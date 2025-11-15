package com.example.venuebookingapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.venuebookingapp.data.local.entity.Booking
import com.example.venuebookingapp.databinding.ItemBookingBinding


class BookingAdapter(
    private val onBookingClick: (Booking) -> Unit
) : ListAdapter<Booking, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            // Bind all the data to your TextViews from item_booking.xml
            // Note: This assumes you will fetch the Venue Name in your fragment or ViewModel.
            // For now, we just show the venue ID.
            binding.tvVenueName.text = "Booking for Venue ID: ${booking.venueId}"
            binding.tvBookingDate.text = "Date: ${booking.bookingDate}"
            binding.tvBookingTime.text = "Time: ${booking.startTime} - ${booking.endTime}"
            binding.tvBookingStatus.text = booking.status

            // Set background color based on status (simplified)
            when (booking.status) {
                "APPROVED" -> binding.tvBookingStatus.setBackgroundColor(0xFF4CAF50.toInt()) // Green
                "REJECTED" -> binding.tvBookingStatus.setBackgroundColor(0xFFF44336.toInt()) // Red
                else -> binding.tvBookingStatus.setBackgroundColor(0xFFFF9800.toInt()) // Orange
            }

            // Click listener for the whole card
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
        val currentBooking = getItem(position)
        holder.bind(currentBooking)
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