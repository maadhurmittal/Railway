package com.example.railway.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.railway.R
import com.google.firebase.auth.FirebaseAuth
import com.example.railway.data.FirebaseRepository // Corrected import
import com.example.railway.data.Item // Corrected import

class MyPostsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerViewItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ItemsAdapter(emptyList()) { item ->
            val intent = Intent(requireContext(), ItemDetailActivity::class.java)
            intent.putExtra("itemId", item.itemId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        FirebaseRepository.fetchMyPosts(uid) { items ->
            adapter.updateList(items)
        }
    }
}