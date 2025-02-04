package io.irfanshadikrishad.slotify.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.adapters.SlotAdapter
import io.irfanshadikrishad.slotify.models.Slot

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var slotRecyclerView: RecyclerView
    private lateinit var slotAdapter: SlotAdapter
    private val slotList = mutableListOf<Slot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_dashboard)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        slotRecyclerView = findViewById(R.id.slotRecyclerView)
        slotRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.createSlotButton).setOnClickListener {
            startActivity(Intent(this, CreateSlotActivity::class.java))
        }

        // Fetch slots initially when the activity is created
        fetchSlots()
    }

    override fun onResume() {
        super.onResume()
        // Re-fetch the slots whenever the activity is resumed
        fetchSlots()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchSlots() {
        val adminId = auth.currentUser?.uid
        if (adminId == null) {
            Toast.makeText(this, "Error: Admin not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("slots").whereEqualTo("adminId", adminId).get()
            .addOnSuccessListener { documents ->
                slotList.clear()
                for (document in documents) {
                    val slot = Slot(
                        id = document.id,
                        adminId = document.getString("adminId") ?: "",
                        date = document.getString("date") ?: "",
                        startTime = document.getString("startTime") ?: "",
                        endTime = document.getString("endTime") ?: "",
                        bookedBy = document.getString("bookedBy")
                    )
                    slotList.add(slot)
                }

                // Update the adapter with the refreshed list
                if (::slotAdapter.isInitialized) {
                    slotAdapter.notifyDataSetChanged()
                } else {
                    slotAdapter = SlotAdapter(this, slotList)
                    slotRecyclerView.adapter = slotAdapter
                }
            }.addOnFailureListener { e ->
                Log.e("AdminDashboard", "Error fetching slots", e)
                Toast.makeText(this, "Failed to load slots", Toast.LENGTH_SHORT).show()
            }
    }
}
