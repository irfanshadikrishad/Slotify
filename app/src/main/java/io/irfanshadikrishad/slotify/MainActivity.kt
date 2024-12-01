package io.irfanshadikrishad.slotify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.activities.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore  // Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Get the current user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // If user is not logged in, redirect to LoginActivity
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Display user information
        val userEmailTextView: TextView = findViewById(R.id.user_email)
        val userNameTextView: TextView = findViewById(R.id.user_name)

        // Set email
        userEmailTextView.text = currentUser.email

        // Fetch user data from Firestore using the UID
        fetchUserData(currentUser.uid, userNameTextView)

        // Handle the logout button click
        val logoutButton: Button = findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun fetchUserData(uid: String, userNameTextView: TextView) {
        // Fetch the user's data from Firestore using their UID
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Check if the 'name' field exists in the Firestore document
                    val userName = document.getString("name")
                    userNameTextView.text =
                        userName ?: "No Name Found"  // Set user name or default text
                } else {
                    Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error getting user data: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    // Logout function
    private fun logout() {
        FirebaseAuth.getInstance().signOut() // Sign out the user
        val intent = Intent(this, LoginActivity::class.java) // Redirect to LoginActivity
        startActivity(intent)
        finish() // Close MainActivity
    }
}
