package com.example.railway.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railway.R
import com.google.firebase.auth.FirebaseAuth
import com.example.railway.data.FirebaseRepository
import com.example.railway.data.Item

class MyPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter
    private lateinit var emptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerViewItems)
        emptyView = view.findViewById(R.id.emptyView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ItemsAdapter(emptyList()) { item ->
            val intent = Intent(requireContext(), ItemDetailActivity::class.java)
            intent.putExtra("itemId", item.itemId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            FirebaseRepository.fetchMyPosts(uid) { items ->
                adapter.updateList(items)
                if (items.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = "You haven't posted any items yet."
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            }
        } else {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = "Please log in to view your posts."
        }
    }
}