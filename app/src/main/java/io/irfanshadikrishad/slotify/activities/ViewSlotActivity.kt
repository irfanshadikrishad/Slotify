package io.irfanshadikrishad.slotify.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R

class ViewSlotActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var bookedByTextView: TextView
    private lateinit var organizationTextView: TextView
    private lateinit var actionButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button

    private var slotId: String? = null
    private var slotAdminId: String? = null
    private var currentUserId: String? = null
    private var isBooked: Boolean = false
    private var bookedBy: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_slot)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        dateTextView = findViewById(R.id.dateTextView)
        timeTextView = findViewById(R.id.timeTextView)
        bookedByTextView = findViewById(R.id.bookedByTextView)
        organizationTextView = findViewById(R.id.organization)
        actionButton = findViewById(R.id.actionButton)
        editButton = findViewById(R.id.editButton)
        deleteButton = findViewById(R.id.deleteButton)

        slotId = intent.getStringExtra("slotId")
        slotAdminId = intent.getStringExtra("adminId")
        currentUserId = auth.currentUser?.uid

        if (slotId == null) {
            Toast.makeText(this, "Error: Slot not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadSlotDetails()

        actionButton.setOnClickListener { toggleBooking() }
        editButton.setOnClickListener { editSlot() }
        deleteButton.setOnClickListener { confirmDeleteSlot() }
        organizationTextView.setOnClickListener {
            Log.i("vsa1087", "Switch clicked")
            try {
                val intent =
                    Intent(this, ProfileActivity::class.java).putExtra("adminID", slotAdminId)
                startActivity(intent)
            } catch (err: Exception) {
                Log.i("vsa1087", "Error switch. ${err.toString()}")
            }
        }
    }

    // For refreshing slot after edit
    override fun onResume() {
        super.onResume()
        loadSlotDetails()
    }

    private fun loadSlotDetails() {
        db.collection("users").document(slotAdminId!!).get().addOnSuccessListener { user ->
            val organization = user.getString("organization").toString()
            organizationTextView.text = organization
        }
        db.collection("slots").document(slotId!!).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val date = document.getString("date") ?: ""
                val startTime = document.getString("startTime") ?: ""
                val endTime = document.getString("endTime") ?: ""
                bookedBy = document.getString("bookedBy")

                dateTextView.text = buildString {
                    append("Date: ")
                    append(date)
                }
                timeTextView.text = buildString {
                    append("Time: ")
                    append(startTime)
                    append(" - ")
                    append(endTime)
                }

                isBooked = bookedBy != null
                if (isBooked) {
                    db.collection("users").document(bookedBy!!).get().addOnSuccessListener { user ->
                        val userName = user.getString("name") ?: "N/A"
                        bookedByTextView.text = buildString {
                            append("Booked by: ")
                            append(userName)
                        }
                    }.addOnFailureListener {
                        bookedByTextView.text = buildString {
                            append("Booked by: Unknown")
                        }
                    }
                } else {
                    bookedByTextView.text = buildString {
                        append("Available")
                    }
                }

                // Admin: Show edit & delete buttons, hide book button
                if (currentUserId == slotAdminId) {
                    editButton.visibility = Button.VISIBLE
                    deleteButton.visibility = Button.VISIBLE
                    actionButton.visibility = Button.GONE
                } else {
                    editButton.visibility = Button.GONE
                    deleteButton.visibility = Button.GONE

                    if (isBooked) {
                        // If the current user booked the slot, show "Unbook Slot"
                        if (bookedBy == currentUserId) {
                            actionButton.text = buildString {
                                append("Unbook Slot")
                            }
                            actionButton.visibility = Button.VISIBLE
                            actionButton.setBackgroundColor(
                                ContextCompat.getColor(
                                    this, R.color.error
                                )
                            )
                        } else {
                            // If another user booked it, hide the button
                            actionButton.visibility = Button.GONE
                        }
                    } else {
                        // If slot is available, show "Book Slot"
                        actionButton.text = buildString {
                            append("Book Slot")
                        }
                        actionButton.visibility = Button.VISIBLE
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load slot", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleBooking() {
        if (isBooked && bookedBy == currentUserId) {
            // Unbook slot
            db.collection("slots").document(slotId!!).update("bookedBy", null)
                .addOnSuccessListener {
                    Toast.makeText(this, "Slot unbooked", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to unbook", Toast.LENGTH_SHORT).show()
                }
        } else if (!isBooked) {
            // Book slot
            db.collection("slots").document(slotId!!).update("bookedBy", currentUserId)
                .addOnSuccessListener {
                    Toast.makeText(this, "Slot booked", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to book", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun editSlot() {
        val intent = Intent(this, CreateSlotActivity::class.java).apply {
            putExtra("slotId", slotId)
            putExtra("date", dateTextView.text.toString().removePrefix("Date: "))
            putExtra(
                "startTime", timeTextView.text.toString().split(" - ")[0].removePrefix("Time: ")
            )
            putExtra("endTime", timeTextView.text.toString().split(" - ")[1])
        }
        startActivity(intent)
    }

    private fun confirmDeleteSlot() {
        AlertDialog.Builder(this).setTitle("Delete Slot")
            .setMessage("Are you sure you want to delete this slot?")
            .setPositiveButton("Yes") { _, _ -> deleteSlot() }.setNegativeButton("No", null).show()
    }

    private fun deleteSlot() {
        db.collection("slots").document(slotId!!).delete().addOnSuccessListener {
            Toast.makeText(this, "Slot deleted", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete slot", Toast.LENGTH_SHORT).show()
        }
    }
}
