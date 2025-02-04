package io.irfanshadikrishad.slotify.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import io.irfanshadikrishad.slotify.R

class UserDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_dashboard)

        val userSlotRecyclerView = findViewById<RecyclerView>(R.id.userSlotRecyclerView)
    }
}
