package com.example.venuebookingapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.venuebookingapp.VenueBookingApplication
import com.example.venuebookingapp.databinding.ActivityLoginBinding
import com.example.venuebookingapp.ui.viewmodel.AuthState
import com.example.venuebookingapp.ui.viewmodel.AuthViewModel
import com.example.venuebookingapp.ui.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // ViewBinding (as per Lecture 4/5)
    private lateinit var binding: ActivityLoginBinding

    // Get the ViewModel using the Factory
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory((application as VenueBookingApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding setup
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Login button click
        binding.btnLogin.setOnClickListener {
            handleLogin()
        }

        // "Sign up" text click
        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Simple validation
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email cannot be empty"
            return
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password cannot be empty"
            return
        } else {
            binding.tilPassword.error = null
        }

        // Call the ViewModel to log in
        authViewModel.login(email, password)
    }

    private fun observeViewModel() {
        // Use lifecycleScope.launch to safely collect the flow (Lecture 9)
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        // Show loading state (e.g., disable button)
                        binding.btnLogin.isEnabled = false
                        binding.btnLogin.text = "Loading..."
                    }
                    is AuthState.Success -> {
                        // Handle success
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = "Login"
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        // Navigate to MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        // Pass user data to the next activity
                        intent.putExtra("USER_ID", state.user.userId)
                        intent.putExtra("USER_ROLE", state.user.role)
                        startActivity(intent)
                        finish() // Close LoginActivity
                    }
                    is AuthState.Error -> {
                        // Handle error
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = "Login"
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthState.RegisterSuccess, is AuthState.Idle -> {
                        // Reset UI
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = "Login"
                    }
                }
            }
        }
    }
}