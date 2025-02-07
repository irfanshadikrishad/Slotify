package io.irfanshadikrishad.slotify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.activities.LoginActivity
import io.irfanshadikrishad.slotify.fragments.AdminDashboardFragment
import io.irfanshadikrishad.slotify.fragments.NotificationsFragment
import io.irfanshadikrishad.slotify.fragments.ProfileFragment
import io.irfanshadikrishad.slotify.fragments.SearchFragment
import io.irfanshadikrishad.slotify.fragments.UserDashboardFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userRole: String? = null

    private lateinit var adminDashboardFragment: AdminDashboardFragment
    private lateinit var userDashboardFragment: UserDashboardFragment
    private lateinit var notificationsFragment: NotificationsFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 🔥 Check if user is logged in
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
            return // Stop execution if user is not logged in
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Initialize fragments
        adminDashboardFragment = AdminDashboardFragment()
        userDashboardFragment = UserDashboardFragment()
        notificationsFragment = NotificationsFragment()
        searchFragment = SearchFragment()
        profileFragment = ProfileFragment()

        getUserRole { role ->
            userRole = role

            // Load the correct default fragment
            val defaultFragment =
                if (role == "admin") adminDashboardFragment else userDashboardFragment
            loadFragment(defaultFragment)

            // Set up navigation listener only after getting role
            setupBottomNavigation()
        }
    }

    private fun getUserRole(callback: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val role = document.getString("role") ?: "user"
            callback(role)
        }.addOnFailureListener {
            callback("user")
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val homeFragment =
                        if (userRole == "admin") adminDashboardFragment else userDashboardFragment
                    loadFragment(homeFragment)
                    true
                }

                R.id.nav_notifications -> {
                    loadFragment(notificationsFragment)
                    true
                }

                R.id.nav_search -> {
                    loadFragment(searchFragment)
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(profileFragment)
                    true
                }

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity so user can't go back without logging in
    }
}
