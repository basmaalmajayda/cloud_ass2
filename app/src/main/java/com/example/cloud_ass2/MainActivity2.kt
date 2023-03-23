package com.example.cloud_ass2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud_ass2.adapters.NoteAdapter
import com.example.cloud_ass2.models.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.notes.*

class MainActivity2 : AppCompatActivity() {
    var analytics = Analytics()
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notes)

        analytics.screenTrack("notes", "notes")

        val catId = intent.getIntExtra("catId", 0)
        val catName = intent.getStringExtra("catName")
        textViewCatNotes.text = catName

        val notes = ArrayList<Note>()

        val myAdapter = NoteAdapter(notes, this)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = myAdapter

        val db = FirebaseFirestore.getInstance()
        if (catId != 0) {
            val docRef = db.collection("notes")
            docRef.get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.getLong("cat_id")?.toInt() == catId)
                            notes.add(
                                Note(
                                    document.id.toInt(),
                                    document.getString("name"),
                                    document.getString("description"),
                                    document.getLong("letters")?.toInt(),
                                    document.getString("image"),
                                    document.getLong("cat_id")?.toInt(),
                                )
                            )
                        Log.e("success", "${document.id} => ${document.data}")
                    }
                    myAdapter.notifyDataSetChanged()
                    if (notes.isEmpty()) {
                        progressBar2.isIndeterminate = true
                        progressBar2.visibility = View.VISIBLE
                    } else {
                        progressBar2.isIndeterminate = false
                        progressBar2.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("error", "Error getting documents.", exception)
                    Toast.makeText(this, "There is an error getting the data", Toast.LENGTH_SHORT)
                }
        }

        if (notes.isEmpty()) {
            progressBar2.visibility = View.VISIBLE
            progressBar2.isIndeterminate = true
        } else {
            progressBar2.visibility = View.GONE
            progressBar2.isIndeterminate = false
        }

        myAdapter.onItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                analytics.selectContent("note${notes[position].id}", "${notes[position].name!!}", "NoteCard")
                val intent = Intent(this@MainActivity2, MainActivity3::class.java)
                intent.putExtra("note", notes[position])
                startActivity(intent)
            }
        })

    }
    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("notes")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }
}