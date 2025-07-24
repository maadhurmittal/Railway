package com.example.railway.data

data class Item(
    val itemId: String = "",
    val type: String = "",          // "lost" or "found"
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val date: String = "",
    val postedBy: String = "",
    val photoUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "pending" // "pending", "claimed", "recovered"
)