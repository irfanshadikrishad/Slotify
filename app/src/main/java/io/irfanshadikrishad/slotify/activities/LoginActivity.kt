package io.irfanshadikrishad.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import io.irfanshadikrishad.slotify.MainActivity
import io.irfanshadikrishad.slotify.R

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var goToRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Views
        emailInput = findViewById(R.id.login_email)
        passwordInput = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.button)
        goToRegister = findViewById(R.id.go_to_register)

        // Set click listener for Login Button
        loginButton.setOnClickListener {
            validateInputsAndLogin()
        }

        // Set click listener for "Go to Register"
        goToRegister.setOnClickListener {
            Log.d("check".toString(), "working".toString())
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputsAndLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (!isValidEmail(email)) {
            emailInput.error = "Please enter a valid email address"
            return
        }

        if (password.isEmpty() || password.length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            return
        }

        // If validation passes, simulate login logic
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

        // Redirect to MainActivity after successful login
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }
}
