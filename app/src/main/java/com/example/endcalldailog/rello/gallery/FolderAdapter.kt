package com.example.myapplication.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class FolderAdapter(private val folders: List<ImageFolder> , private val onClick: (ImageFolder) -> Unit) :
    RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.folderImage)
        val nameView: TextView = view.findViewById(R.id.folderName)
        val pathView: TextView = view.findViewById(R.id.folderPath)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun getItemCount(): Int = folders.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.nameView.text = folder.name
        holder.pathView.text = folder.path

        Glide.with(holder.itemView.context)
            .load(folder.firstImageUri)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onClick(folder)
        }
    }
}
