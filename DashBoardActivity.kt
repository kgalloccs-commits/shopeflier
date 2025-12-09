package com.shopeflier.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.shopeflier.app.databinding.ActivityDashboardBinding
import com.shopeflier.app.fragments.*
import com.shopeflier.app.utils.UserManager

class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var userManager: UserManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userManager = UserManager(this)
        
        // Check if user is logged in
        if (!userManager.isLoggedIn()) {
            navigateToLogin()
            return
        }
        
        setupUI()
        setupBottomNavigation()
        
        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }
    
    private fun setupUI() {
        val currentUser = userManager.getCurrentUser()
        currentUser?.let {
            binding.tvUserName.text = "Hello, ${it.name}"
        }
        
        binding.btnLogout.setOnClickListener {
            handleLogout()
        }
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_browse -> {
                    loadFragment(BrowseFragment())
                    true
                }
                R.id.nav_sell -> {
                    loadFragment(SellFragment())
                    true
                }
                R.id.nav_messages -> {
                    loadFragment(MessagesFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
    
    private fun handleLogout() {
        userManager.logoutUser()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
