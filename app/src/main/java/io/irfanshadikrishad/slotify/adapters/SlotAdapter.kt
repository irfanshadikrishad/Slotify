package io.irfanshadikrishad.slotify.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.models.Slot

class SlotAdapter(private val context: Context, private val slotList: List<Slot>) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

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
        holder.bookedByTextView.text =
            if (slot.bookedBy != null) "Booked By: ${slot.bookedBy}" else "Available"

        // Click Listener
        holder.itemView.setOnClickListener {
            Log.i("sa1087", "Slot clicked - ID: ${slot.id}, AdminID: ${slot.adminId}")

            Toast.makeText(
                context, "Slot selected: ${slot.date} ${slot.startTime}", Toast.LENGTH_SHORT
            ).show()

        }
    }

    override fun getItemCount(): Int = slotList.size
}
