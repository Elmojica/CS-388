package com.rkpandey.parselergram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.core.content.FileProvider
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

class MainActivity : AppCompatActivity() {
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1. Setting the description of the post
        //2. A button to launch the camera to take a picture
        //3. An ImageView to show the picture the user has taken
        //4. A button to save and send the post to our Parse server
        findViewById<Button>(R.id.btnSubmit).setOnClickListener{
            //send post to server without an image
            //Get the description that they have inputted
            val description = findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if(photoFile != null){
                submitPost(description,user, photoFile!!)
                Toast.makeText(this, "Successfuly submitted post", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "You must take a picture.", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "No picture taken")
            }
        }
        findViewById<Button>(R.id.btnTakePicture).setOnClickListener{
            //Launch camera to let user take picture
            onLaunchCamera()
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener{
            item->
            when(item.itemId){
                R.id.action_home->{
                    //TODO: Navigate to the home screen
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.action_compose->{
                    //TODO: Navigate to the compose screen
                    Toast.makeText(this, "Compose", Toast.LENGTH_SHORT).show()
                }
                R.id.action_profile->{
                    //TODO: Navigate to the profile screen
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                }

            }

            true
        }

        queryPosts()

    }
    fun submitPost(description: String, user:ParseUser, file: File){
        //Create the Post object
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        val pb = findViewById<ProgressBar>(R.id.pbLoading)
        pb.visibility = ProgressBar.VISIBLE
        post.saveInBackground{ exception ->
            if(exception != null){
                Log.e(TAG, "Error while saving post")
                exception.printStackTrace()
                Toast.makeText(this, "Error while saving post", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Successfully saved post")
                //TODO: Resetting the EditText field to be empty
                //TODO: Reset the ImageView to empty
            }
        }
        pb.visibility = ProgressBar.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            }else{
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
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

