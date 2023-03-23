package com.example.cloud_ass2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cloud_ass2.adapters.CategoryAdapter
import com.example.cloud_ass2.models.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.home.*

class MainActivity : AppCompatActivity() {
    var analytics = Analytics()
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        analytics.screenTrack("home", "home")

        val categories = ArrayList<Category>()

        val myAdapter = CategoryAdapter(categories, this)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this)
        categoryRecyclerView.adapter = myAdapter

        val db = FirebaseFirestore.getInstance()
        db.collection("categories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    categories.add(
                        Category(
                            document.id.toInt(),
                            document.getString("name"),
                            document.getString("image"),
                        )
                    )
                    Log.e("success", "${document.id} => ${document.data}")
                }
                myAdapter.notifyDataSetChanged()
                if (categories.isEmpty()) {
                    progressBar.isIndeterminate = true
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.isIndeterminate = false
                    progressBar.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("error", "Error getting documents.", exception)
                Toast.makeText(this, "There is an error getting the data", Toast.LENGTH_SHORT)
            }

//        val storageRef = FirebaseStorage.getInstance().getReference()

        if (categories.isEmpty()) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.isIndeterminate = false
            progressBar.visibility = View.GONE
        }

        myAdapter.onItemClickListener(object : CategoryAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                analytics.selectContent("category${categories[position].id}", "${categories[position].name!!}", "CategoryCard")
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                intent.putExtra("catId", categories[position].id)
                intent.putExtra("catName", categories[position].name)
                print(categories[position].id)
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //start
        analytics.trackScreenView("home")
    }

    override fun onPause() {
        super.onPause()
        //end
        analytics.trackScreenDuration()
    }

}