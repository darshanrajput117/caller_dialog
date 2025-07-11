package com.example.myapplication.gallery

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityFolderImagesBinding

class FolderImagesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderImagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val folderPath = intent.getStringExtra("folder_path") ?: return

        val imageUris = getAllImagesFromFolder(folderPath)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = ImageAdapter(imageUris){clickedPosition ->
            val intent = Intent(this, ImageSliderActivity::class.java)
            intent.putParcelableArrayListExtra("image_uris", ArrayList(imageUris))
            intent.putExtra("start_position", clickedPosition)
            startActivity(intent)

        }


    }

    private fun getAllImagesFromFolder(folderPath: String): List<Uri> {
        val uriList = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )
        val selection = "${MediaStore.Images.Media.DATA} LIKE ?"
        val selectionArgs = arrayOf("%$folderPath%")

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (it.moveToNext()) {
                val imageId = it.getLong(idCol)
                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId
                )
                uriList.add(imageUri)
            }
        }

        return uriList
    }
}
