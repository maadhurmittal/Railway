package com.example.railway.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.railway.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var welcomeTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        welcomeTextView = findViewById(R.id.welcomeTextView)
        logoutButton = findViewById(R.id.logoutButton)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            welcomeTextView.text = "Welcome, ${currentUser.email?.substringBefore("@") ?: "User"}!"
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        if (savedInstanceState == null) {
            loadFragment(ItemsFragment.newInstance(type = "lost"))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_lost -> {
                    loadFragment(ItemsFragment.newInstance(type = "lost"))
                    true
                }
                R.id.menu_found -> {
                    loadFragment(ItemsFragment.newInstance(type = "found"))
                    true
                }
                R.id.menu_post -> {
                    startActivity(Intent(this, PostItemActivity::class.java))
                    true
                }
                R.id.menu_my_posts -> {
                    loadFragment(MyPostsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}