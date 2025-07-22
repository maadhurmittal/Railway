package com.example.railway.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.railway.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val lostFragment = ItemsFragment.newInstance("lost")
    private val foundFragment = ItemsFragment.newInstance("found")
    private val postFragment = PostItemFragment()
    private val myPostsFragment = MyPostsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_lost -> openFragment(lostFragment)
                R.id.menu_found -> openFragment(foundFragment)
                R.id.menu_post -> openFragment(postFragment)
                R.id.menu_my_posts -> openFragment(myPostsFragment)
            }
            true
        }

        // Set default fragment
        bottomNav.selectedItemId = R.id.menu_lost
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
