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

        // Date picker for dateEdit
        dateEdit.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, { _, y, m, d ->
                dateEdit.setText("$y-${m + 1}-$d")
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
        val postedBy = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || date.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select image", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseRepository.uploadImage(imageUri!!) { success, url ->
            if (success && url != null) {
                val item = Item(
                    type = type,
                    title = title,
                    description = description,
                    category = category,
                    location = location,
                    date = date,
                    postedBy = postedBy,
                    photoUrl = url
                )
                FirebaseRepository.postItem(item) { ok, err ->
                    if (ok) {
                        Toast.makeText(this, "Post Uploaded", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Post Failed: $err", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}