package io.irfanshadikrishad.slotify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.activities.ProfileActivity
import io.irfanshadikrishad.slotify.models.User

class OrganizationAdapter(private val context: Context, private var orgList: List<User>) :
    RecyclerView.Adapter<OrganizationAdapter.OrganizationViewHolder>() {

    class OrganizationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orgNameTextView: TextView = itemView.findViewById(R.id.organization_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.organization_item, parent, false)
        return OrganizationViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrganizationViewHolder, position: Int) {
        val user = orgList[position]
        holder.orgNameTextView.text = user.organization

        // Navigate to ProfileActivity on click
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("adminID", user.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orgList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<User>) {
        orgList = newList
        notifyDataSetChanged()
    }
}
