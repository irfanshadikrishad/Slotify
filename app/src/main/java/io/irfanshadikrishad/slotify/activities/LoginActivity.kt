package io.irfanshadikrishad.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import io.irfanshadikrishad.slotify.MainActivity
import io.irfanshadikrishad.slotify.R

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var goToRegister: TextView

    private lateinit var mAuth: FirebaseAuth  // Firebase Auth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Initialize views
        emailInput = findViewById(R.id.login_email)
        passwordInput = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.button)
        goToRegister = findViewById(R.id.go_to_register)

        // Set up login button click listener
        loginButton.setOnClickListener {
            validateInputsAndSignIn()
        }

        // Set up redirection to RegisterActivity
        goToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputsAndSignIn() {
        val email = emailInput.text?.toString()?.trim() ?: ""
        val password = passwordInput.text?.toString()?.trim() ?: ""

        // Validate email
        if (!isValidEmail(email)) {
            emailInput.error = "Please enter a valid email address"
            return
        }

        // Validate password
        if (password.isEmpty()) {
            passwordInput.error = "Password cannot be empty"
            return
        }

        // Sign in user using Firebase Auth
        signInUser(email, password)
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun signInUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign-in successful
                val user = mAuth.currentUser
                Toast.makeText(this, "Welcome back, ${user?.email}!", Toast.LENGTH_SHORT).show()

                // Redirect to the main activity or home screen
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()  // Finish LoginActivity
            } else {
                // If sign-in fails, display a message to the user
                Toast.makeText(
                    this, "Sign-in failed: ${task.exception?.message}", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
