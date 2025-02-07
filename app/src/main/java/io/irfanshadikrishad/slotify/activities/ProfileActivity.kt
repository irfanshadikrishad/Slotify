package io.irfanshadikrishad.slotify.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.adapters.SlotAdapter
import io.irfanshadikrishad.slotify.models.Slot

class ProfileActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var organizationTextView: TextView
    private lateinit var slotRecyclerView: RecyclerView
    private lateinit var slotAdapter: SlotAdapter
    private val slotList = mutableListOf<Slot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.organization)

        val adminID = intent.getStringExtra("adminID")

        if (adminID == null) {
            Toast.makeText(this, "Error: Admin ID not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        firestore = FirebaseFirestore.getInstance()
        organizationTextView = findViewById(R.id.organization_name)
        slotRecyclerView = findViewById(R.id.recycler)

        slotRecyclerView.layoutManager = LinearLayoutManager(this)
        slotAdapter = SlotAdapter(this, slotList)
        slotRecyclerView.adapter = slotAdapter

        loadAdminData(adminID)
        loadSlots(adminID)
    }

    private fun loadAdminData(adminID: String) {
        try {
            firestore.collection("users").document(adminID).get().addOnSuccessListener { user ->
                val orgName = user.getString("organization") ?: "Unknown Organization"
                organizationTextView.text = orgName
            }.addOnFailureListener { _ ->
                Toast.makeText(this, "Failed to load organization", Toast.LENGTH_SHORT).show()
            }
        } catch (error: Exception) {
            Log.i("pa1087", "Error. $error")
        }
    }

    private fun loadSlots(adminID: String) {
        try {
            firestore.collection("slots").whereEqualTo("adminId", adminID).get()
                .addOnSuccessListener { documents ->
                    val tempList = mutableListOf<Slot>()

                    for (document in documents) {
                        val slot = Slot(
                            id = document.id,
                            adminId = document.getString("adminId") ?: "",
                            date = document.getString("date") ?: "",
                            startTime = document.getString("startTime") ?: "",
                            endTime = document.getString("endTime") ?: "",
                            bookedBy = document.getString("bookedBy")
                        )

                        tempList.add(slot)
                    }

                    slotAdapter.updateData(tempList)
                }.addOnFailureListener { e ->
                    Log.e("pa1087", "Error fetching slots", e)
                }
        } catch (error: Exception) {
            Log.i("pa1087", "Error. $error")
        }
    }

}
