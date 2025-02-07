package io.irfanshadikrishad.slotify.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.irfanshadikrishad.slotify.R
import io.irfanshadikrishad.slotify.adapters.OrganizationAdapter
import io.irfanshadikrishad.slotify.models.User

class SearchFragment : Fragment() {
    private lateinit var searchInput: EditText
    private lateinit var searchResultsRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var orgAdapter: OrganizationAdapter
    private val userList = mutableListOf<User>()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchInput = view.findViewById(R.id.searchInput)
        searchResultsRecycler = view.findViewById(R.id.searchResultsRecycler)
        progressBar = view.findViewById(R.id.progressBar)

        orgAdapter = OrganizationAdapter(requireContext(), userList)
        searchResultsRecycler.layoutManager = LinearLayoutManager(requireContext())
        searchResultsRecycler.adapter = orgAdapter

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    searchOrganizations(query)
                } else {
                    userList.clear()
                    orgAdapter.updateData(userList)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchOrganizations(query: String) {
        val searchQuery = query.lowercase()
        progressBar.visibility = View.VISIBLE

        firestore.collection("users").get().addOnSuccessListener { documents ->
            Log.i("sea1087", "success")
            userList.clear()
            for (doc in documents) {
                val orgName = doc.getString("organization") ?: ""
                if (orgName.lowercase().contains(searchQuery)) { // Case-insensitive filtering
                    val user = User(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown",
                        organization = orgName
                    )
                    Log.i("sea1087", user.toString())
                    userList.add(user)
                }
            }
            orgAdapter.updateData(userList)
            progressBar.visibility = View.GONE
        }.addOnFailureListener { e ->
            Log.e("sea1087", "Error searching", e)
            progressBar.visibility = View.GONE
        }
    }

}
