package com.example.venuebookingapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.venuebookingapp.R
import com.example.venuebookingapp.databinding.ActivityMainBinding
import com.example.venuebookingapp.ui.fragments.VenuesFragment
import com.example.venuebookingapp.ui.fragments.BookingsFragment
import com.example.venuebookingapp.ui.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Store user data passed from LoginActivity
    private var userId: Int = -1
    private lateinit var userRole: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user data from LoginActivity's Intent
        userId = intent.getIntExtra("USER_ID", -1)
        userRole = intent.getStringExtra("USER_ROLE") ?: "CLIENT"

        // Set up the Bottom Navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_venues -> {
                    loadFragment(VenuesFragment())
                    true
                }

                R.id.nav_bookings -> {
                    loadFragment(BookingsFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        // Load the default fragment (Venues) when the activity is first created
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_venues
        }
    }


    private fun loadFragment(fragment: Fragment) {

        val bundle = Bundle()
        bundle.putInt("USER_ID", userId)
        bundle.putString("USER_ROLE", userRole)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}