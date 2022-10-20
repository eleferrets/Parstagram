package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parstagram.fragments.ComposeFragment
import com.example.parstagram.fragments.FeedFragment
import com.example.parstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File


/**
 * Let user create a post by taking a camera photo
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queryPosts()
        val fragmentManager: FragmentManager = supportFragmentManager
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            // Alias
            item ->
var fragmentToShow: Fragment? = null
            when (item.itemId) {
                R.id.action_home -> {
                    fragmentToShow = FeedFragment()
//                    Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show()
                }
                R.id.action_compose -> {
                    // Set it to the fragment we want to show
                    fragmentToShow = ComposeFragment()
//                    Toast.makeText(this,"Compose",Toast.LENGTH_SHORT).show()

                }
                R.id.action_profile -> {
                    fragmentToShow = ProfileFragment()
//                    Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show()

                }
            }
            if (fragmentToShow != null) {
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }
            // Return true when we handled the interaction
            true
        }
        // Set default selection
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home
    }

    // Inflate the menu to show the app bar icon
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // What happens when this is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            logoutUser()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            goToLoginActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        // End app after using back button by closing this activity
        finish()
    }

    private fun logoutUser() {
        ParseUser.logOut()
        val currentUser = ParseUser.getCurrentUser() // this will now be null
    }









    // Query for all posts
    fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something went wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        // If we got something
                        for (post in posts) {
                            Log.i(
                                TAG,
                                "Post: " + post.getDescription() + " , username: " + post.getUser()?.username
                            )
                        }
                    }
                }
            }
        })

    }

    companion object {
        const val TAG = "MainActivity"
    }
}