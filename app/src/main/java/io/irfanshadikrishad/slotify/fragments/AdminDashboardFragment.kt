package io.irfanshadikrishad.slotify.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.activities.CreateSlotActivity
import io.irfanshadikrishad.slotify.adapters.SlotAdapter
import io.irfanshadikrishad.slotify.models.Slot

class AdminDashboardFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var slotRecyclerView: RecyclerView
    private lateinit var slotAdapter: SlotAdapter
    private val slotList = mutableListOf<Slot>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        slotRecyclerView = view.findViewById(R.id.slotRecyclerView)
        slotRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.createSlotButton).setOnClickListener {
            startActivity(Intent(requireContext(), CreateSlotActivity::class.java))
        }

        fetchSlots()
    }

    override fun onResume() {
        super.onResume()
        fetchSlots()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchSlots() {
        val adminId = auth.currentUser?.uid
        if (adminId == null) {
            Toast.makeText(requireContext(), "Error: Admin not logged in", Toast.LENGTH_SHORT)
                .show()
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

                if (::slotAdapter.isInitialized) {
                    slotAdapter.notifyDataSetChanged()
                } else {
                    slotAdapter = SlotAdapter(requireContext(), slotList)
                    slotRecyclerView.adapter = slotAdapter
                }
            }.addOnFailureListener { e ->
                Log.e("AdminDashboardFragment", "Error fetching slots", e)
                Toast.makeText(requireContext(), "Failed to load slots", Toast.LENGTH_SHORT).show()
            }
    }
}
