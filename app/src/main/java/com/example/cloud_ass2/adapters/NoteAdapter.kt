package com.example.cloud_ass2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloud_ass2.R
import com.example.cloud_ass2.models.Note
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.note_card.view.*

class NoteAdapter(private val list: ArrayList<Note>, var context: Context) : RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    private lateinit var mlistener: OnItemClickListener

    fun onItemClickListener(listener: OnItemClickListener){
        mlistener = listener
    }

    class ViewHolder (itemView: View, listener: OnItemClickListener):RecyclerView.ViewHolder(itemView) {
        init{
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
        val name = itemView.noteName
        val image = itemView.noteImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note_card,parent,false)
        return ViewHolder(itemView, mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //هنا بحط كود الضغط على الكاردة
        val current = list[position]
        holder.name.text = current.name

        val storageImage = FirebaseStorage.getInstance().reference
        val storageRef = storageImage.child(current.image.toString())
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Picasso.with(context).load(imageUrl).into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}