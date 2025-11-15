package com.example.venuebookingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.databinding.FragmentProfileBinding
import com.example.venuebookingapp.ui.activity.LoginActivity
import com.example.venuebookingapp.ui.viewmodel.AuthViewModel
import com.example.venuebookingapp.ui.viewmodel.AuthViewModelFactory
import com.example.venuebookingapp.ui.viewmodel.ProfileViewModel
import com.example.venuebookingapp.ui.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModel for fetching user data
    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory((requireActivity().application as VenueBookingApplication).userRepository)
    }

    // ViewModel for handling logout
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((requireActivity().application as VenueBookingApplication).userRepository)
    }

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("USER_ID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the user's data
        if (userId != -1) {
            profileViewModel.loadUser(userId)
        }

        observeViewModel()
        setupLogoutButton()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.user.collect { user ->
                if (user != null) {
                    binding.tvProfileName.text = user.name
                    binding.tvProfileEmail.text = user.email
                    binding.tvProfileRole.text = "Role: ${user.role}"
                }
            }
        }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            // Call the logout function on the AuthViewModel
            authViewModel.logout()

            // Navigate back to LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding
    }
}