package io.irfanshadikrishad.slotify.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.irfanshadikrishad.slotify.R

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_dashboard)

        val createSlotButton = findViewById<Button>(R.id.createSlotButton)
        val slotRecyclerView = findViewById<RecyclerView>(R.id.slotRecyclerView)

        createSlotButton.setOnClickListener {
            startActivity(Intent(this, CreateSlotActivity::class.java))
        }
    }
}
