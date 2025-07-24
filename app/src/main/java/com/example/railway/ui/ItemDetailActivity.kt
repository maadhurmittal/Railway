package com.example.railway.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.railway.R
import com.example.railway.data.FirebaseRepository
import com.example.railway.data.Item

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var itemTitle: TextView
    private lateinit var itemDescription: TextView
    private lateinit var itemCategory: TextView
    private lateinit var itemLocation: TextView
    private lateinit var itemDate: TextView
    private lateinit var itemPostedBy: TextView
    private lateinit var itemPhoto: ImageView
    private lateinit var itemStatus: TextView

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

        val itemId = intent.getStringExtra("itemId")

        itemId?.let {
            // Fetch item details from Firebase based on itemId
            FirebaseRepository.firestore.collection("items").document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val item = documentSnapshot.toObject(Item::class.java)
                        item?.let {
                            itemTitle.text = it.title
                            itemDescription.text = it.description
                            itemCategory.text = "Category: ${it.category}"
                            itemLocation.text = "Location: ${it.location}"
                            itemDate.text = "Date: ${it.date}"
                            itemStatus.text = "Status: ${it.status.replaceFirstChar { char -> char.uppercase() }}"

                            // Fetch user name
                            FirebaseRepository.firestore.collection("users").document(it.postedBy)
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
                                .load(it.photoUrl)
                                .placeholder(R.drawable.ic_image_placeholder)
                                .error(R.drawable.ic_error_image)
                                .into(itemPhoto)
                        }
                    } else {
                        Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching item: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } ?: run {
            Toast.makeText(this, "Item ID missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}