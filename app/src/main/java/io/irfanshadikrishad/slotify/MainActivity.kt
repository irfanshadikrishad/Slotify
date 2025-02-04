package io.irfanshadikrishad.slotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.fragments.AdminDashboardFragment
import io.irfanshadikrishad.slotify.fragments.NotificationsFragment
import io.irfanshadikrishad.slotify.fragments.ProfileFragment
import io.irfanshadikrishad.slotify.fragments.UserDashboardFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        getUserRole { role ->
            userRole = role
            val defaultFragment =
                if (role == "admin") AdminDashboardFragment() else UserDashboardFragment()
            loadFragment(defaultFragment)  // Open the correct home fragment by default
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val homeFragment =
                        if (userRole == "admin") AdminDashboardFragment() else UserDashboardFragment()
                    loadFragment(homeFragment)
                    true
                }

                R.id.nav_notifications -> {
                    loadFragment(NotificationsFragment())
                    true
                }

                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun getUserRole(callback: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val role = document.getString("role") ?: "user"
            callback(role)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
    }
}
