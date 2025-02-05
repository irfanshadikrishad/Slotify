package io.irfanshadikrishad.slotify.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.activities.ViewSlotActivity
import io.irfanshadikrishad.slotify.models.Slot

class SlotAdapter(private val context: Context, private val slotList: List<Slot>) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    private val db = FirebaseFirestore.getInstance() // Firestore instance

    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val bookedByTextView: TextView = itemView.findViewById(R.id.bookedByTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slot_item, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slotList[position]

        // Set TextViews
        holder.dateTextView.text = buildString {
            append("Date: ")
            append(slot.date)
        }
        holder.timeTextView.text = buildString {
            append("Time: ")
            append(slot.startTime)
            append(" - ")
            append(slot.endTime)
        }

        if (slot.bookedBy != null) {
            // Fetch user's name from Firestore
            db.collection("users").document(slot.bookedBy).get().addOnSuccessListener { document ->
                val userName = document.getString("name") ?: "Unknown"
                holder.bookedByTextView.text = buildString {
                    append("Booked By: ")
                    append(userName)
                }
            }.addOnFailureListener {
                holder.bookedByTextView.text = buildString {
                    append("Booked By: Unknown")
                }
            }
        } else {
            holder.bookedByTextView.text = buildString {
                append("Available")
            }
        }

        // Click Listener: Open ViewSlotActivity
        holder.itemView.setOnClickListener {
            try {
                val intent = Intent(context, ViewSlotActivity::class.java).apply {
                    putExtra("slotId", slot.id)
                    putExtra("adminId", slot.adminId)
                }
                context.startActivity(intent)
            } catch (error: Exception) {
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = slotList.size
}
