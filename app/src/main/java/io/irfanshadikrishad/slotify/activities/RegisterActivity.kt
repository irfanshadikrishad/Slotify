package io.irfanshadikrishad.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import io.irfanshadikrishad.slotify.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var goToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        nameInput = findViewById(R.id.register_name)
        emailInput = findViewById(R.id.register_email) // Corrected id
        passwordInput = findViewById(R.id.register_password)
        confirmPasswordInput = findViewById(R.id.register_password_2)
        registerButton = findViewById(R.id.button)
        goToLogin = findViewById(R.id.go_to_register)

        // Set up register button click listener
        registerButton.setOnClickListener {
            validateInputsAndRegister()
        }

        // Set up redirection to LoginActivity
        goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputsAndRegister() {
        val name = nameInput.text?.toString()?.trim() ?: ""
        val email = emailInput.text?.toString()?.trim() ?: ""
        val password = passwordInput.text?.toString()?.trim() ?: ""
        val confirmPassword = confirmPasswordInput.text?.toString()?.trim() ?: ""

        // Validate name
        if (name.isEmpty()) {
            nameInput.error = "Name cannot be empty"
            return
        }

        // Validate email
        if (!isValidEmail(email)) {
            emailInput.error = "Please enter a valid email address"
            return
        }

        // Validate password
        if (password.isEmpty() || password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            return
        }

        // Validate confirm password
        if (confirmPassword != password) {
            confirmPasswordInput.error = "Passwords do not match"
            return
        }

        // Simulate registration success
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

        // Redirect to LoginActivity after registration
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }
}
