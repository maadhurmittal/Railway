package com.example.railway.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.railway.R
import com.example.railway.data.FirebaseRepository
import com.example.railway.data.Item
import com.google.firebase.auth.FirebaseAuth
import android.view.View

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var itemTitle: TextView
    private lateinit var itemDescription: TextView
    private lateinit var itemCategory: TextView
    private lateinit var itemLocation: TextView
    private lateinit var itemDate: TextView
    private lateinit var itemPostedBy: TextView
    private lateinit var itemPhoto: ImageView
    private lateinit var itemStatus: TextView
    private lateinit var btnMarkAsFound: Button
    private lateinit var btnDeleteItem: Button

    private var currentItem: Item? = null
    private var itemId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        itemTitle = findViewById(R.id.detailItemTitle)
        itemDescription = findViewById(R.id.detailItemDescription)
        itemCategory = findViewById(R.id.detailItemCategory)
        itemLocation = findViewById(R.id.detailItemLocation)
        itemDate = findViewById(R.id.detailItemDate)
        itemPostedBy = findViewById(R.id.detailItemPostedBy)
        itemPhoto = findViewById(R.id.detailItemPhoto)
        itemStatus = findViewById(R.id.detailItemStatus)
        btnMarkAsFound = findViewById(R.id.btnMarkAsFound)
        btnDeleteItem = findViewById(R.id.btnDeleteItem)

        itemId = intent.getStringExtra("itemId")

        setupListeners()
        fetchItemDetails()
    }

    private fun setupListeners() {
        btnMarkAsFound.setOnClickListener {
            currentItem?.let { item ->
                if (item.status != "recovered") {
                    updateItemStatus(item.itemId, "recovered")
                } else {
                    Toast.makeText(this, "Item is already marked as recovered.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnDeleteItem.setOnClickListener {
            currentItem?.let { item ->
                showDeleteConfirmationDialog(item.itemId)
            }
        }
    }

    private fun fetchItemDetails() {
        itemId?.let { id ->
            FirebaseRepository.firestore.collection("items").document(id)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        Toast.makeText(this, "Error fetching item: ${error.message}", Toast.LENGTH_SHORT).show()
                        finish()
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val item = documentSnapshot.toObject(Item::class.java)
                        item?.let {
                            currentItem = it
                            displayItemDetails(it)
                            updateActionButtonsVisibility(it)
                        }
                    } else {
                        Toast.makeText(this, "Item not found or has been deleted.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
        } ?: run {
            Toast.makeText(this, "Item ID missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayItemDetails(item: Item) {
        itemTitle.text = item.title
        itemDescription.text = item.description
        itemCategory.text = "Category: ${item.category}"
        itemLocation.text = "Location: ${item.location}"
        itemDate.text = "Date: ${item.date}"
        itemStatus.text = "Status: ${item.status.replaceFirstChar { char -> char.uppercase() }}"

        FirebaseRepository.firestore.collection("users").document(item.postedBy)
            .get()
            .addOnSuccessListener { userDocument ->
                if (userDocument.exists()) {
                    val userName = userDocument.getString("name")
                    itemPostedBy.text = "Posted By: $userName"
                } else {
                    itemPostedBy.text = "Posted By: Unknown User"
                }
            }
            .addOnFailureListener {
                itemPostedBy.text = "Posted By: Error fetching user"
            }

        Glide.with(this)
            .load(item.photoUrl)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_error_image)
            .into(itemPhoto)
    }

    private fun updateActionButtonsVisibility(item: Item) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid == item.postedBy) {
            btnDeleteItem.visibility = View.VISIBLE
            if (item.type == "lost" && item.status == "pending") {
                btnMarkAsFound.visibility = View.VISIBLE
            } else {
                btnMarkAsFound.visibility = View.GONE
            }
        } else {
            btnDeleteItem.visibility = View.GONE
            btnMarkAsFound.visibility = View.GONE
        }
    }

    private fun updateItemStatus(itemId: String, newStatus: String) {
        FirebaseRepository.updateStatus(itemId, newStatus) { success ->
            if (success) {
                Toast.makeText(this, "Item status updated to $newStatus!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update item status.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(itemId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item? This action cannot be undone.")
            .setPositiveButton("Delete") { dialog, which ->
                deleteItem(itemId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItem(itemId: String) {
        FirebaseRepository.deleteItem(itemId) { success ->
            if (success) {
                Toast.makeText(this, "Item deleted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to delete item.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}