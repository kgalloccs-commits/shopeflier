package com.shopeflier.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shopeflier.app.databinding.ActivityEditProfileBinding
import com.shopeflier.app.utils.UserManager

class EditProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userManager: UserManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userManager = UserManager(this)
        setupUI()
    }
    
    private fun setupUI() {
        // Load current user data
        val currentUser = userManager.getCurrentUser()
        currentUser?.let { user ->
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone)
        }
        
        binding.btnSave.setOnClickListener {
            saveProfile()
        }
        
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }
        
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }
        
        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone is required"
            return
        }
        
        // Update user profile
        val currentUser = userManager.getCurrentUser()
        currentUser?.let { user ->
            val updatedUser = user.copy(
                name = name,
                email = email,
                phone = phone
            )
            
            if (userManager.updateUser(updatedUser)) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
