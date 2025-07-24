package com.example.railway.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

// import data.Item // Removed
// import data.User // Removed


object FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
     val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Register user and save to Firestore
    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    onResult(false, "User ID not found.")
                    return@addOnSuccessListener
                }

                val user = User(uid, name, email, "user")
                firestore.collection("users").document(uid)
                    .set(user)
                    .addOnSuccessListener {
                        onResult(true, null)
                    }
                    .addOnFailureListener { e ->
                        onResult(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    // Login user
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    // Upload image to Firebase Storage and return download URL
    fun uploadImage(uri: Uri, onResult: (Boolean, String?) -> Unit) {
        val ref = storage.reference.child("item_images/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
                ref.downloadUrl
            }
            .addOnSuccessListener { url ->
                onResult(true, url.toString())
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    // Post item to Firestore
    fun postItem(item: Item, onResult: (Boolean, String?) -> Unit) {
        val docRef = firestore.collection("items").document()
        val newItem = item.copy(itemId = docRef.id)
        docRef.set(newItem)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    // Fetch lost/found items by type
    fun fetchItems(type: String, onItems: (List<Item>) -> Unit) {
        firestore.collection("items")
            .whereEqualTo("type", type)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onItems(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot.toObjects(Item::class.java)
                onItems(items)
            }
    }

    // Fetch current user's posts
    fun fetchMyPosts(uid: String, onItems: (List<Item>) -> Unit) {
        firestore.collection("items")
            .whereEqualTo("postedBy", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onItems(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot.toObjects(Item::class.java)
                onItems(items)
            }
    }

    // Update status of item (for admin/owner)
    fun updateStatus(itemId: String, status: String, onResult: (Boolean) -> Unit) {
        firestore.collection("items").document(itemId)
            .update("status", status)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Delete item (for admin/owner)
    fun deleteItem(itemId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("items").document(itemId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}