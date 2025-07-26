package com.example.railway.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.railway.R
import com.example.railway.data.FirebaseRepository
import com.example.railway.data.Item
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class PostItemActivity : AppCompatActivity() {

    private lateinit var typeSpinner: Spinner
    private lateinit var titleEdit: EditText
    private lateinit var descriptionEdit: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var locationEdit: EditText
    private lateinit var dateEdit: EditText
    private lateinit var uploadBtn: Button
    private lateinit var postBtn: Button
    private lateinit var itemImageView: ImageView

    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_item)

        typeSpinner = findViewById(R.id.spinnerType)
        titleEdit = findViewById(R.id.editTitle)
        descriptionEdit = findViewById(R.id.editDescription)
        categorySpinner = findViewById(R.id.spinnerCategory)
        locationEdit = findViewById(R.id.editLocation)
        dateEdit = findViewById(R.id.editDate)
        uploadBtn = findViewById(R.id.btnUploadImage)
        postBtn = findViewById(R.id.btnPost)
        itemImageView = findViewById(R.id.item_image_preview)

        ArrayAdapter.createFromResource(
            this,
            R.array.item_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.item_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

        dateEdit.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, y, m, d ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(y, m, d)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                dateEdit.setText(dateFormat.format(selectedDate.time))
            }, year, month, day)
            dpd.show()
        }

        uploadBtn.setOnClickListener {
            openFileChooser()
        }

        postBtn.setOnClickListener {
            postItem()
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            itemImageView.setImageURI(imageUri)
            itemImageView.visibility = ImageView.VISIBLE
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun postItem() {
        val type = typeSpinner.selectedItem.toString().lowercase(Locale.getDefault())
        val title = titleEdit.text.toString().trim()
        val description = descriptionEdit.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val location = locationEdit.text.toString().trim()
        val date = dateEdit.text.toString().trim()
        val postedBy = FirebaseAuth.getInstance().currentUser?.uid

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || date.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_LONG).show()
            return
        }
        if (postedBy == null) {
            Toast.makeText(this, "You must be logged in to post an item.", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Uploading image and posting item...", Toast.LENGTH_LONG).show()

        FirebaseRepository.uploadImage(imageUri!!) { success, photoUrl ->
            if (success && photoUrl != null) {
                val item = Item(
                    type = type,
                    title = title,
                    description = description,
                    category = category,
                    location = location,
                    date = date,
                    postedBy = postedBy,
                    photoUrl = photoUrl,
                    timestamp = System.currentTimeMillis(),
                    status = "pending"
                )

                FirebaseRepository.postItem(item) { postOk, errorMessage ->
                    if (postOk) {
                        Toast.makeText(this, "Item Posted Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to post item: ${errorMessage ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Image upload failed. Please try again.", Toast.LENGTH_LONG).show()
            }
        }
    }
}