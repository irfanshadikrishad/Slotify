package io.irfanshadikrishad.slotify.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.text.*

class CreateSlotActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var dateTextView: TextView
    private lateinit var startTimeTextView: TextView
    private lateinit var endTimeTextView: TextView

    private var selectedDate: String? = null
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null
    private var slotId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_slot)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val selectDateButton = findViewById<Button>(R.id.selectDateButton)
        val selectStartTimeButton = findViewById<Button>(R.id.selectStartTimeButton)
        val selectEndTimeButton = findViewById<Button>(R.id.selectEndTimeButton)
        val createSlotButton = findViewById<Button>(R.id.createSlotButton)

        dateTextView = findViewById(R.id.dateTextView)
        startTimeTextView = findViewById(R.id.startTimeTextView)
        endTimeTextView = findViewById(R.id.endTimeTextView)

        selectDateButton.setOnClickListener { showDatePicker() }
        selectStartTimeButton.setOnClickListener { showTimePicker(true) }
        selectEndTimeButton.setOnClickListener { showTimePicker(false) }

        createSlotButton.setOnClickListener { createOrUpdateSlot() }

        slotId = intent.getStringExtra("slotId")

        if (slotId != null) {
            // Editing an existing slot
            val date = intent.getStringExtra("date") ?: ""
            val startTime = intent.getStringExtra("startTime") ?: ""
            val endTime = intent.getStringExtra("endTime") ?: ""

            dateTextView.text = date
            startTimeTextView.text = startTime
            endTimeTextView.text = endTime

            createSlotButton.text = buildString {
                append("Update Slot")
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = "$year-${month + 1}-$dayOfMonth"
                dateTextView.text = buildString {
                    append("Selected Date: ")
                    append(selectedDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(this, { _, hour, minute ->
            val formattedTime = String.format("%02d:%02d", hour, minute)

            if (isStartTime) {
                selectedStartTime = formattedTime
                startTimeTextView.text = buildString {
                    append("Start Time: ")
                    append(selectedStartTime)
                }
            } else {
                selectedEndTime = formattedTime
                endTimeTextView.text = buildString {
                    append("End Time: ")
                    append(selectedEndTime)
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)

        timePicker.show()
    }

    private fun createOrUpdateSlot() {
        // Check if the selected values are null or empty
        val date =
            selectedDate ?: dateTextView.text.toString().replace("Selected Date: ", "").trim()
        val startTime =
            selectedStartTime ?: startTimeTextView.text.toString().replace("Start Time: ", "")
                .trim()
        val endTime =
            selectedEndTime ?: endTimeTextView.text.toString().replace("End Time: ", "").trim()
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // If any of the fields are empty, show a toast asking the user to fill all fields
        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please select a date and time for all fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Query existing slots for the same date
        db.collection("slots").whereEqualTo("date", date).get().addOnSuccessListener { result ->
            for (document in result) {
                if (slotId != null && document.id == slotId) continue // Ignore the same slot if editing

                val existingStartTime = document.getString("startTime") ?: continue
                val existingEndTime = document.getString("endTime") ?: continue

                // Check for time overlap
                if (isTimeOverlap(startTime, endTime, existingStartTime, existingEndTime)) {
                    Toast.makeText(this, "Time overlaps with another slot", Toast.LENGTH_SHORT)
                        .show()
                    return@addOnSuccessListener
                }
            }

            val slotData = hashMapOf(
                "adminId" to adminId, "date" to date, "startTime" to startTime, "endTime" to endTime
            )

            if (slotId == null) {
                // Create new slot
                db.collection("slots").add(slotData).addOnSuccessListener {
                    Toast.makeText(this, "Slot created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to create slot", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing slot
                db.collection("slots").document(slotId!!).update(slotData as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Slot updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to update slot", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to check existing slots", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper function to check time overlap
    private fun isTimeOverlap(
        newStart: String, newEnd: String, existingStart: String, existingEnd: String
    ): Boolean {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val newStartTime = timeFormat.parse(newStart)?.time ?: return false
            val newEndTime = timeFormat.parse(newEnd)?.time ?: return false
            val existingStartTime = timeFormat.parse(existingStart)?.time ?: return false
            val existingEndTime = timeFormat.parse(existingEnd)?.time ?: return false

            return (newStartTime < existingEndTime && newEndTime > existingStartTime)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}
