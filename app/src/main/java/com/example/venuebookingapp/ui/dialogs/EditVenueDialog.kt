package com.example.venuebookingapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.venuebookingapp.databinding.DialogAddVenueBinding
import com.example.venuebookingapp.data.local.entity.Venue

class EditVenueDialog : DialogFragment() {

    private var _binding: DialogAddVenueBinding? = null
    private val binding get() = _binding!!

    private lateinit var venue: Venue
    private var onVenueUpdatedListener: ((Venue) -> Unit)? = null

    companion object {
        private const val ARG_VENUE_ID = "venue_id"
        private const val ARG_OWNER_ID = "owner_id"
        private const val ARG_NAME = "name"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_LOCATION = "location"
        private const val ARG_CAPACITY = "capacity"
        private const val ARG_PRICE = "price"
        private const val ARG_AMENITIES = "amenities"
        private const val ARG_IMAGE_URL = "image_url"
        private const val ARG_STATUS = "status" // ✅ 1. ADDED STATUS KEY

        fun newInstance(venue: Venue) = EditVenueDialog().apply {
            arguments = Bundle().apply {
                putInt(ARG_VENUE_ID, venue.venueId)
                putInt(ARG_OWNER_ID, venue.ownerId)
                putString(ARG_NAME, venue.name)
                putString(ARG_DESCRIPTION, venue.description)
                putString(ARG_LOCATION, venue.location)
                putInt(ARG_CAPACITY, venue.capacity)
                putDouble(ARG_PRICE, venue.pricePerDay)
                putString(ARG_AMENITIES, venue.amenities)
                putString(ARG_IMAGE_URL, venue.imageUrl)
                putString(ARG_STATUS, venue.status) // ✅ 2. ADDED STATUS TO BUNDLE
            }
        }
    }

    fun setOnVenueUpdatedListener(listener: (Venue) -> Unit) {
        onVenueUpdatedListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            venue = Venue(
                venueId = it.getInt(ARG_VENUE_ID),
                ownerId = it.getInt(ARG_OWNER_ID),
                name = it.getString(ARG_NAME) ?: "",
                description = it.getString(ARG_DESCRIPTION) ?: "",
                location = it.getString(ARG_LOCATION) ?: "",
                capacity = it.getInt(ARG_CAPACITY),
                pricePerDay = it.getDouble(ARG_PRICE),
                amenities = it.getString(ARG_AMENITIES) ?: "",
                imageUrl = it.getString(ARG_IMAGE_URL),
                status = it.getString(ARG_STATUS) ?: "PENDING"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddVenueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Change title to "Edit Venue"
        binding.tvDialogTitle.text = "Edit Venue"
        binding.btnSave.text = "Update"

        // Pre-fill existing data
        binding.etVenueName.setText(venue.name)
        binding.etDescription.setText(venue.description)
        binding.etLocation.setText(venue.location)
        binding.etCapacity.setText(venue.capacity.toString())
        binding.etPrice.setText(venue.pricePerDay.toString())
        binding.etAmenities.setText(venue.amenities)
        binding.etImageUrl.setText(venue.imageUrl)

        binding.btnSave.setOnClickListener {
            updateVenue()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun updateVenue() {
        val name = binding.etVenueName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val capacityStr = binding.etCapacity.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val amenities = binding.etAmenities.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim().ifEmpty { null } // Use null if empty

        when {
            name.isEmpty() -> {
                Toast.makeText(context, "Please enter venue name", Toast.LENGTH_SHORT).show()
                return
            }
            location.isEmpty() -> {
                Toast.makeText(context, "Please enter location", Toast.LENGTH_SHORT).show()
                return
            }
            capacityStr.isEmpty() -> {
                Toast.makeText(context, "Please enter capacity", Toast.LENGTH_SHORT).show()
                return
            }
            priceStr.isEmpty() -> {
                Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val capacity = capacityStr.toIntOrNull() ?: 0
        val price = priceStr.toDoubleOrNull() ?: 0.0

        if (capacity <= 0 || price <= 0) {
            Toast.makeText(context, "Please enter valid capacity and price", Toast.LENGTH_SHORT).show()
            return
        }

        // Use .copy() to create the updated object.
        // This preserves the original venueId, ownerId, and status.
        val updatedVenue = venue.copy(
            name = name,
            description = description,
            location = location,
            capacity = capacity,
            pricePerDay = price,
            amenities = amenities,
            imageUrl = imageUrl
        )

        onVenueUpdatedListener?.invoke(updatedVenue)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}