package com.shopeflier.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shopeflier.app.databinding.ActivityMainBinding
import com.shopeflier.app.models.User
import com.shopeflier.app.utils.UserManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var userManager: UserManager
    private var isLoginMode = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        userManager = UserManager(this)
        
        // Check if user is already logged in
        if (userManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }
        
        setupUI()
        setupClickListeners()
    }
    
    private fun setupUI() {
        updateFormMode()
    }
    
    private fun setupClickListeners() {
        binding.btnToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            updateFormMode()
            clearForm()
        }
        
        binding.btnSubmit.setOnClickListener {
            if (isLoginMode) {
                handleLogin()
            } else {
                handleRegister()
            }
        }
    }
    
    private fun updateFormMode() {
        if (isLoginMode) {
            binding.tvTitle.text = "Welcome Back!"
            binding.tvSubtitle.text = "Sign in to your Shopeflier account"
            binding.btnSubmit.text = "Login"
            binding.btnToggleMode.text = "Don't have an account? Register"
            
            // Hide registration fields
            binding.layoutName.visibility = android.view.View.GONE
            binding.layoutPhone.visibility = android.view.View.GONE
            binding.layoutConfirmPassword.visibility = android.view.View.GONE
        } else {
            binding.tvTitle.text = "Join Shopeflier"
            binding.tvSubtitle.text = "Create your account to start trading"
            binding.btnSubmit.text = "Register"
            binding.btnToggleMode.text = "Already have an account? Login"
            
            // Show registration fields
            binding.layoutName.visibility = android.view.View.VISIBLE
            binding.layoutPhone.visibility = android.view.View.VISIBLE
            binding.layoutConfirmPassword.visibility = android.view.View.VISIBLE
        }
    }
    
    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (validateLoginInput(email, password)) {
            val user = userManager.loginUser(email, password)
            if (user != null) {
                Toast.makeText(this, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            } else {
                showError("Invalid email or password. Please try again.")
            }
        }
    }
    
    private fun handleRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        
        if (validateRegisterInput(name, email, phone, password, confirmPassword)) {
            val user = User(
                name = name,
                email = email,
                phone = phone,
                password = password,
                registrationDate = System.currentTimeMillis()
            )
            
            if (userManager.registerUser(user)) {
                Toast.makeText(this, "Registration successful! Welcome to Shopeflier!", Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            } else {
                showError("Email already exists. Please use a different email.")
            }
        }
    }
    
    private fun validateLoginInput(email: String, password: String): Boolean {
        when {
            email.isEmpty() -> {
                showError("Please enter your email")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Please enter a valid email address")
                return false
            }
            password.isEmpty() -> {
                showError("Please enter your password")
                return false
            }
        }
        return true
    }
    
    private fun validateRegisterInput(
        name: String, 
        email: String, 
        phone: String, 
        password: String, 
        confirmPassword: String
    ): Boolean {
        when {
            name.isEmpty() -> {
                showError("Please enter your full name")
                return false
            }
            email.isEmpty() -> {
                showError("Please enter your email")
                return false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Please enter a valid email address")
                return false
            }
            phone.isEmpty() -> {
                showError("Please enter your phone number")
                return false
            }
            phone.length < 10 -> {
                showError("Please enter a valid phone number")
                return false
            }
            password.isEmpty() -> {
                showError("Please enter a password")
                return false
            }
            password.length < 6 -> {
                showError("Password must be at least 6 characters long")
                return false
            }
            confirmPassword.isEmpty() -> {
                showError("Please confirm your password")
                return false
            }
            password != confirmPassword -> {
                showError("Passwords do not match")
                return false
            }
        }
        return true
    }
    
    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = android.view.View.VISIBLE
        
        // Hide error after 3 seconds
        binding.tvError.postDelayed({
            binding.tvError.visibility = android.view.View.GONE
        }, 3000)
    }
    
    private fun clearForm() {
        binding.etName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPhone.text?.clear()
        binding.etPassword.text?.clear()
        binding.etConfirmPassword.text?.clear()
        binding.tvError.visibility = android.view.View.GONE
    }
    
    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
