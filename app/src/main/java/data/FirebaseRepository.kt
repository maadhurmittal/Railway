package data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

object FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Register user and save in Firestore
    fun register(name: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.uid!!
                val user = User(uid, name, email, "user")
                firestore.collection("users").document(uid).set(user)
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    // Login user
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // Upload item image and get URL
    fun uploadImage(uri: Uri, onResult: (Boolean, String?) -> Unit) {
        val ref = storage.reference.child("item_images/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception!!
            ref.downloadUrl
        }.addOnSuccessListener { url ->
            onResult(true, url.toString())
        }.addOnFailureListener {
            onResult(false, null)
        }
    }

    // Post item document in Firestore
    fun postItem(item: Item, onResult: (Boolean, String?) -> Unit) {
        val doc = firestore.collection("items").document()
        val newItem = item.copy(itemId = doc.id)
        doc.set(newItem)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    // Fetch all lost or found items by type ("lost" or "found")
    fun fetchItems(type: String, onItems: (List<Item>) -> Unit) {
        firestore.collection("items")
            .whereEqualTo("type", type)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.toObjects(Item::class.java) ?: emptyList()
                onItems(items)
            }
    }

    // Fetch current user’s own posts
    fun fetchMyPosts(uid: String, onItems: (List<Item>) -> Unit) {
        firestore.collection("items")
            .whereEqualTo("postedBy", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val items = snapshot?.toObjects(Item::class.java) ?: emptyList()
                onItems(items)
            }
    }

    // Update item status (admin/owner)
    fun updateStatus(itemId: String, status: String, onResult: (Boolean) -> Unit) {
        firestore.collection("items").document(itemId)
            .update("status", status)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Delete item (admin/owner)
    fun deleteItem(itemId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("items").document(itemId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}