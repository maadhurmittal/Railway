package com.example.railway.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.GoogleAuthProvider

object FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                val email = authResult.user?.email
                val displayName = authResult.user?.displayName

                if (uid != null && email != null && displayName != null) {
                    val user = User(uid, displayName, email, "user")
                    firestore.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener {
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                } else {
                    onResult(false, "Google Sign-In failed: User data incomplete.")
                }
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }

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

    fun fetchItemById(itemId: String, onResult: (Item?) -> Unit) {
        firestore.collection("items").document(itemId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val item = documentSnapshot.toObject(Item::class.java)
                    onResult(item)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

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

    fun updateStatus(itemId: String, status: String, onResult: (Boolean) -> Unit) {
        firestore.collection("items").document(itemId)
            .update("status", status)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteItem(itemId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("items").document(itemId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }
}