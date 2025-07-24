package com.example.railway.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.railway.R
import com.example.railway.data.Item

class ItemsAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    fun updateList(newList: List<Item>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.itemTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.itemDescription)
        private val locationTextView: TextView = itemView.findViewById(R.id.itemLocation)
        private val dateTextView: TextView = itemView.findViewById(R.id.itemDate)
        private val photoImageView: ImageView = itemView.findViewById(R.id.itemPhoto)

        fun bind(item: Item) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            locationTextView.text = "📍 ${item.location}"
            dateTextView.text = "📅 ${item.date}"

            Glide.with(itemView.context)
                .load(item.photoUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_error_image)
                .into(photoImageView)

            // 🔽 Set click listener with ripple support
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
