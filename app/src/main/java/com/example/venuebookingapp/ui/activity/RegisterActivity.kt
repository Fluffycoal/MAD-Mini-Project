package com.example.venuebookingapp.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.venuebookingapp.R
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.databinding.ActivityRegisterBinding
import com.example.venuebookingapp.ui.viewmodel.AuthState
import com.example.venuebookingapp.ui.viewmodel.AuthViewModel
import com.example.venuebookingapp.ui.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityRegisterBinding

    // Get the ViewModel using the Factory
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as VenueBookingApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding setup
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // "Sign up" button click
        binding.btnSignup.setOnClickListener {
            handleRegistration()
        }

        // "Login" text click
        binding.tvLogin.setOnClickListener {
            finish() // Closes this activity and returns to LoginActivity
        }
    }

    private fun handleRegistration() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val selectedRoleId = binding.rgRole.checkedRadioButtonId
        val role = if (selectedRoleId == R.id.rbClient) "CLIENT" else "VENUE_OWNER"

        // --- Start Validation ---
        if (name.isEmpty()) {
            binding.tilName.error = "Name cannot be empty"
            return
        } else {
            binding.tilName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email cannot be empty"
            return
        } else {
            binding.tilEmail.error = null
        }

        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone cannot be empty"
            return
        } else {
            binding.tilPhone.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password cannot be empty"
            return
        } else {
            binding.tilPassword.error = null
        }

        if (confirmPassword != password) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            return
        } else {
            binding.tilConfirmPassword.error = null
        }
        // --- End Validation ---

        // Call the ViewModel to register
        authViewModel.register(name, email, phone, password, role)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Show loading state
                        binding.btnSignup.isEnabled = false
                        binding.btnSignup.text = "Creating Account..."
                    }
                    is AuthState.RegisterSuccess -> {
                        // Handle success
                        binding.btnSignup.isEnabled = true
                        binding.btnSignup.text = "Sign Up"
                        Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        finish() // Close RegisterActivity, go back to Login
                    }
                    is AuthState.Error -> {
                        // Handle error
                        binding.btnSignup.isEnabled = true
                        binding.btnSignup.text = "Sign Up"
                        Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthState.Success, is AuthState.Idle -> {
                        // Reset UI
                        binding.btnSignup.isEnabled = true
                        binding.btnSignup.text = "Sign Up"
                    }
                }
            }
        }
    }
}