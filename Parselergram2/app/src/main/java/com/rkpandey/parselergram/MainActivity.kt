package com.rkpandey.parselergram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import com.rkpandey.parselergram.fragments.ComposeFragment
import com.rkpandey.parselergram.fragments.FeedFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener{
            item->
            var fragmentToShow: Fragment? = null
            when(item.itemId){
                R.id.action_home->{
                    //TODO: Navigate to the home screen/feed fragment
                    fragmentToShow = FeedFragment()
                }
                R.id.action_compose->{
                    //TODO: Navigate to the compose screen
                    fragmentToShow = ComposeFragment()
                }
                R.id.action_profile->{
                    //TODO: Navigate to the profile screen
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }

            }
            if(fragmentToShow != null){
                fragmentManager.beginTransaction().replace(R.id.flContainer,fragmentToShow).commit()
            }
            true
        }
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.action_home
        queryPosts()
    }


    //query for all posts in our server
    private fun queryPosts() {
        //specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        //find all post objects
        query.include(Post.KEY_USER)
        query.findInBackground(object : FindCallback<Post>{
            override fun done(posts: MutableList<Post>?, e:ParseException?){
                if(e != null){
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null){
                        for(post in posts){
                            Log.i(TAG, "Post:" + post.getDescription()+ ", username: "+ post.getUser()?.username)
                        }
                    }
                }

            }
        })

    }
    companion object{
        const val TAG = "MainActivity"
    }

}

