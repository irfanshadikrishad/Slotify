package io.irfanshadikrishad.slotify.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.adapters.SlotAdapter
import io.irfanshadikrishad.slotify.models.Slot

class UserDashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var slotAdapter: SlotAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val slots = mutableListOf<Slot>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.user_dashboard, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchSlots() // for refreshing
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.userSlotRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        slotAdapter = SlotAdapter(requireContext(), slots)
        recyclerView.adapter = slotAdapter

        fetchSlots()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchSlots() {
        db.collection("slots").get().addOnSuccessListener { documents ->
            slots.clear()
            for (document in documents) {
                val slot = document.toObject(Slot::class.java).copy(id = document.id)
                slots.add(slot)
            }
            slotAdapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load slots", Toast.LENGTH_SHORT).show()
        }
    }
}
