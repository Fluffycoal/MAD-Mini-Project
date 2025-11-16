package com.example.venuebookingapp.ui.dialogs

import android.app.Dialog
import android.util.Log
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.venuebookingapp.databinding.DialogAddVenueBinding
import com.example.venuebookingapp.data.local.entity.Venue

class AddVenueDialog : DialogFragment() {

    private var _binding: DialogAddVenueBinding? = null
    private val binding get() = _binding!!

    private var ownerId: Int = 0
    private var onVenueAddedListener: ((Venue) -> Unit)? = null

    companion object {
        private const val ARG_OWNER_ID = "USER_ID"

        fun newInstance(ownerId: Int) = AddVenueDialog().apply {
            arguments = Bundle().apply {
                putInt(ARG_OWNER_ID, ownerId)
            }
        }
    }

    fun setOnVenueAddedListener(listener: (Venue) -> Unit) {
        onVenueAddedListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ownerId = it.getInt(ARG_OWNER_ID)
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

        binding.btnSave.setOnClickListener {
            saveVenue()
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

    private fun saveVenue() {
        Log.d("VenueDebug", "Owner ID passed: $ownerId")
        val name = binding.etVenueName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val capacityStr = binding.etCapacity.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val amenities = binding.etAmenities.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()

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

        val venue = Venue(
            ownerId = ownerId,
            name = name,
            description = description,
            location = location,
            capacity = capacity,
            pricePerDay = price,
            amenities = amenities,
            imageUrl = imageUrl,
            status = "APPROVED"
        )

        onVenueAddedListener?.invoke(venue)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
