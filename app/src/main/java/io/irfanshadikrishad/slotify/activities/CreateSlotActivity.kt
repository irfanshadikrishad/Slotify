package io.irfanshadikrishad.slotify.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R

class CreateSlotActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_slot)

        db = FirebaseFirestore.getInstance()

        val slotTimeEditText = findViewById<EditText>(R.id.slotTimeEditText)
        val saveSlotButton = findViewById<Button>(R.id.saveSlotButton)

        saveSlotButton.setOnClickListener {
            val slotTime = slotTimeEditText.text.toString().trim()

            if (slotTime.isNotEmpty()) {
                val slot = hashMapOf(
                    "time" to slotTime, "isBooked" to false
                )

                db.collection("slots").add(slot).addOnSuccessListener {
                        Toast.makeText(this, "Slot added!", Toast.LENGTH_SHORT).show()
                        finish() // Go back to Admin Dashboard
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to add slot", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Enter a valid slot time", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
