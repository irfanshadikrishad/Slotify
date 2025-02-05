package io.irfanshadikrishad.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)
        val orgEditText = findViewById<EditText>(R.id.orgEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginLink = findViewById<TextView>(R.id.loginLink)

        // Populate role spinner
        ArrayAdapter.createFromResource(
            this, R.array.user_roles, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roleSpinner.adapter = adapter
        }

        // Show organization name field only if role is Admin
        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedRole = roleSpinner.selectedItem.toString().lowercase()
                orgEditText.visibility = if (selectedRole == "admin") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val role = roleSpinner.selectedItem.toString().lowercase()
            val orgName = orgEditText.text.toString().trim()

            if (!validateInputs(name, email, password)) return@setOnClickListener

            checkIfUserExists(email) { exists ->
                if (exists) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show()
                } else {
                    registerUser(name, email, password, role, orgName)
                }
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
    }

    private fun checkIfUserExists(email: String, callback: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                callback(signInMethods.isNotEmpty()) // If non-empty, user exists
            } else {
                callback(false)
            }
        }
    }

    private fun registerUser(
        name: String, email: String, password: String, role: String, orgName: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val user = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "role" to role,
                        "organization" to if (role == "admin") orgName else null
                    )

                    db.collection("users").document(userId).set(user).addOnSuccessListener {
                        sendVerificationEmail()
                    }
                }
            } else {
                Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendVerificationEmail() {
        val user: FirebaseUser? = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this, "Verification email sent. Please check your inbox.", Toast.LENGTH_LONG
                ).show()
                auth.signOut() // Sign out so they don't log in without verification
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
