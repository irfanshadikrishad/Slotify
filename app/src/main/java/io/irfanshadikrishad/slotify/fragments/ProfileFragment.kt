package io.irfanshadikrishad.slotify.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.activities.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var roleTextView: TextView
    private lateinit var organizationTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchUserProfile()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameTextView = view.findViewById(R.id.username)
        emailTextView = view.findViewById(R.id.email)
        roleTextView = view.findViewById(R.id.role)
        organizationTextView = view.findViewById(R.id.organization)
        logoutButton = view.findViewById(R.id.logout)

        fetchUserProfile()

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish() // Close current activity
        }
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"
                    val role = document.getString("role") ?: "N/A"
                    val organization = document.getString("organization") ?: ""

                    usernameTextView.text = name
                    emailTextView.text = email
                    roleTextView.text = buildString {
                        append("Role: ")
                        append(role)
                    }

                    if (role == "admin") {
                        organizationTextView.text = buildString {
                            append("Organization: ")
                            append(organization)
                        }
                        organizationTextView.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch user data", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
