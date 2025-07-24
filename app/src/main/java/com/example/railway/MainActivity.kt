package com.example.railway.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.railway.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val lostFragment = ItemsFragment.newInstance("lost")
    private val foundFragment = ItemsFragment.newInstance("found")
    private val myPostsFragment = MyPostsFragment()

    private var currentFragmentTag = "lost"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_lost -> switchToFragment(lostFragment, "lost")
                R.id.menu_found -> switchToFragment(foundFragment, "found")
                R.id.menu_post -> {
                    startActivity(Intent(this, PostItemActivity::class.java))
                    return@setOnItemSelectedListener true
                }
                R.id.menu_my_posts -> switchToFragment(myPostsFragment, "my_posts")
                else -> false
            }
        }

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.menu_lost
        }
    }

    private fun switchToFragment(fragment: Fragment, tag: String): Boolean {
        if (tag == currentFragmentTag) return true // Already selected

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()

        currentFragmentTag = tag
        return true
    }
}
