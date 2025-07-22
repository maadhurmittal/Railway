package com.example.railway.ui
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
import com.example.railway.data.FirebaseRepository
import com.example.railway.data.Item

class ItemsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemsAdapter
    private var itemType: String = "lost"

    companion object {
        private const val ARG_TYPE = "type"

        fun newInstance(type: String): ItemsFragment {
            val fragment = ItemsFragment()
            val args = Bundle()
            args.putString(ARG_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemType = arguments?.getString(ARG_TYPE) ?: "lost"
    }

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

        FirebaseRepository.fetchItems(itemType) { items ->
            adapter.updateList(items)
        }
    }
}
