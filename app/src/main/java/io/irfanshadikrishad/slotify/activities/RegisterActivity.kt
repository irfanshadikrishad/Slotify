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
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.MainActivity
import io.irfanshadikrishad.slotify.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var goToLogin: TextView

    private lateinit var mAuth: FirebaseAuth  // Firebase Auth instance
    private lateinit var db: FirebaseFirestore  // Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        nameInput = findViewById(R.id.register_name)
        emailInput = findViewById(R.id.register_email)
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

        // Register user using Firebase Auth
        registerUser(email, password, name)
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun registerUser(email: String, password: String, name: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Registration successful
                val user = mAuth.currentUser
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                // Store the user's name and other details in Firestore
                val userMap = hashMapOf(
                    "name" to name, "email" to email
                )
                user?.let {
                    // Store the user's information under their user ID in Firestore
                    db.collection("users").document(it.uid).set(userMap).addOnSuccessListener {
                        // Redirect to LoginActivity after saving user data
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()  // Finish RegisterActivity
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this, "Error saving user data: ${e.message}", Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                // If registration fails, display a message to the user
                Toast.makeText(
                    this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
