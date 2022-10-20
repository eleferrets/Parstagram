package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.parstagram.EndlessRecyclerViewScrollListener
import com.example.parstagram.Post
import com.example.parstagram.PostAdapter
import com.example.parstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery

open class FeedFragment : Fragment() {
    lateinit var postsRecyclerView: RecyclerView
    lateinit var adapter: PostAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var scrollListener: EndlessRecyclerViewScrollListener


    var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }
// Instantiate that view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    // View setup
    postsRecyclerView = view.findViewById(R.id.postRecyclerView)

adapter= PostAdapter(requireContext(), allPosts)
    postsRecyclerView.adapter = adapter
    var linearLayoutManager = LinearLayoutManager(requireContext())
    postsRecyclerView.layoutManager = linearLayoutManager
    swipeContainer = view.findViewById(R.id.swipeContainer)
    swipeContainer.setOnRefreshListener {
        Log.i(TAG, "Refreshing timeline")
        allPosts.clear()
        queryPosts()
    }
    // Configure the refreshing colors
    swipeContainer.setColorSchemeResources(
        android.R.color.holo_blue_bright,
        android.R.color.holo_green_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_red_light
    )
    scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
            // Triggered only when new data needs to be appended to the list
            // Add whatever code is needed to append new items to the bottom of the list
            loadMorePosts()
            Log.i(TAG, "onLoadMore: $page")
        }
    }
    postsRecyclerView.addOnScrollListener(scrollListener)


    queryPosts()
}

    private fun loadMorePosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        // Newer posts appear first since it's descending
        query.addDescendingOrder("createdAt")
        // Only return the most recent 20 posts
        query.limit = 20
        query.skip = allPosts.size
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
                        Log.i(TAG,allPosts.size.toString())
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    // Query for all posts
    open fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        // Newer posts appear first since it's descending
        query.addDescendingOrder("createdAt")
        // Only return the most recent 20 posts
        query.setLimit(20)
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
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                        swipeContainer.setRefreshing(false)
                    }
                }
            }
        })

    }

    companion object {
        const val TAG = "MainActivity"
    }
}