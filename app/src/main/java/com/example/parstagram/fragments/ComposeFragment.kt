package com.example.parstagram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.R
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File


class ComposeFragment : Fragment() {
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    lateinit var ivPreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set onclicklisteners and setup

        ivPreview = view.findViewById(R.id.imageView)


        // Returns the File for a photo stored on disk given the fileName
        fun getPhotoFileUri(fileName: String): File {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            val mediaStorageDir =
                File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(MainActivity.TAG, "failed to create directory")
            }

            // Return the file target for the photo based on filename
            return File(mediaStorageDir.path + File.separator + fileName)
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
                    FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    // Start the image capture intent to take photo
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
                }
            }
        }
        // Send a post object to our Parse server
        fun submitPost(description: String, user: ParseUser, file: File) {
            // Create the Post object
            // on some click or some loading we need to wait for...
            // on some click or some loading we need to wait for...
            val pb = view.findViewById<View>(R.id.pbLoading) as ProgressBar
            pb.visibility = ProgressBar.VISIBLE
            val post = Post()
            post.setDescription(description)
            post.setUser(user)
            post.setImage(ParseFile(file))
            post.saveInBackground { exception ->
                if (exception != null) {
                    Log.e(MainActivity.TAG, "Error while saving post")
                    exception.printStackTrace()
                    Toast.makeText(requireContext(), "Error saving post", Toast.LENGTH_SHORT).show()

                } else {
                    Log.i(MainActivity.TAG, "Successfully saved post")
                    // Reset views
                    view.findViewById<EditText>(R.id.description).setText("")
                    view.findViewById<ImageView>(R.id.imageView).setImageBitmap(null)

                }
                pb.visibility = ProgressBar.INVISIBLE
            }
        }
        // Set description of post
        // Button to launch camera for picture
        // Image to show the picture
        // A button to save and send the post
        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // Send post to server
            // Get description
            val description = view.findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                // Double exclamation points mean file is guaranteed not to be null
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(MainActivity.TAG, "Error getting picture")
                Toast.makeText(requireContext(), "Take a picture!", Toast.LENGTH_SHORT).show()


            }
        }
        view.findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // We have photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // Resize bitmap
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage)
            } else {
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}