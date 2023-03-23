package com.example.cloud_ass2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.cloud_ass2.models.Note
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.details.*

class MainActivity3 : AppCompatActivity() {
    var analytics = Analytics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details)

        analytics.screenTrack("details", "details")

//        var noteId = intent.getStringExtra("noteId")

        var myNote = intent.getParcelableExtra<Note>("note")
        noteTitle.text = myNote!!.name
        noteDescription.text = myNote.description
        noteLetters.text = myNote.letters.toString()

        myNoteImage.visibility = View.GONE
        progressBar3.visibility = View.VISIBLE

        val storageImage = FirebaseStorage.getInstance().reference
        val storageRef = storageImage.child(myNote.image.toString())

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Picasso.with(this).load(imageUrl).into(myNoteImage)
            myNoteImage.visibility = View.VISIBLE
            progressBar3.visibility = View.GONE
            Log.e("success", "get image successfully")
        }.addOnFailureListener { exception ->
            Log.e("error", "Error getting documents.", exception)
            Toast.makeText(this, "There is an error getting the image", Toast.LENGTH_SHORT)
        }
    }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("details")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }
}