package io.irfanshadikrishad.slotify.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
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

        createSlotButton.setOnClickListener { createSlot() }
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

    private fun createSlot() {
        Log.i("cs1087", "createSlot called")
        if (selectedDate == null || selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Please select date and time range", Toast.LENGTH_SHORT).show()
            return
        }

        val adminId = auth.currentUser?.uid
        Log.i("cs1087", "createSlot $adminId")

        // Validate if the new slot overlaps with any existing ones
        db.collection("slots").whereEqualTo("date", selectedDate).get()
            .addOnSuccessListener { documents ->
                Log.i("cs1087", "createSlot on success listener called")
                for (document in documents) {
                    val existingStart = document.getString("startTime")
                    val existingEnd = document.getString("endTime")

                    if (existingStart != null && existingEnd != null) {
                        if (isTimeOverlap(
                                selectedStartTime!!, selectedEndTime!!, existingStart, existingEnd
                            )
                        ) {
                            Toast.makeText(
                                this, "Slot time conflicts with an existing slot", Toast.LENGTH_LONG
                            ).show()
                            return@addOnSuccessListener
                        }
                    }
                }

                // If no conflicts, add the slot
                val slot = hashMapOf(
                    "adminId" to adminId,
                    "date" to selectedDate,
                    "startTime" to selectedStartTime,
                    "endTime" to selectedEndTime,
                    "bookedBy" to null
                )

                db.collection("slots").add(slot).addOnSuccessListener {
                    Log.i("cs1087", "createSlot add functionality called")
                    Toast.makeText(this, "Slot created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Log.i("cs1087", "createSlot add slot failed called")
                    Toast.makeText(this, "Failed to create slot", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Helper function to check time overlap
    private fun isTimeOverlap(
        newStart: String, newEnd: String, existingStart: String, existingEnd: String
    ): Boolean {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val newStartTime = timeFormat.parse(newStart)!!.time
        val newEndTime = timeFormat.parse(newEnd)!!.time
        val existingStartTime = timeFormat.parse(existingStart)!!.time
        val existingEndTime = timeFormat.parse(existingEnd)!!.time

        return (newStartTime < existingEndTime && newEndTime > existingStartTime)
    }
}
