package com.example.venuebookingapp.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.databinding.DialogAddReviewBinding
import com.example.venuebookingapp.ui.viewmodel.ReviewViewModel
import com.example.venuebookingapp.ui.viewmodel.ReviewViewModelFactory

class AddReviewDialog : DialogFragment() {

    private var _binding: DialogAddReviewBinding? = null
    private val binding get() = _binding!!

    // Use ReviewViewModel to submit the review directly
    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory((requireActivity().application as VenueBookingApplication).reviewRepository)
    }

    private var venueId: Int = -1
    private var clientId: Int = -1

    companion object {
        private const val ARG_VENUE_ID = "venue_id"
        private const val ARG_CLIENT_ID = "client_id"

        fun newInstance(venueId: Int, clientId: Int) = AddReviewDialog().apply {
            arguments = Bundle().apply {
                putInt(ARG_VENUE_ID, venueId)
                putInt(ARG_CLIENT_ID, clientId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            venueId = it.getInt(ARG_VENUE_ID)
            clientId = it.getInt(ARG_CLIENT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubmitReview.setOnClickListener {
            submitReview()
        }
    }

    private fun submitReview() {
        val rating = binding.ratingBar.rating // Rating is a Float
        val comment = binding.etComment.text.toString().trim()

        if (rating == 0f) {
            Toast.makeText(context, "Please select a rating", Toast.LENGTH_SHORT).show()
            return
        }

        // Call the ViewModel to add the review
        reviewViewModel.addReview(venueId, clientId, rating, comment)

        Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}