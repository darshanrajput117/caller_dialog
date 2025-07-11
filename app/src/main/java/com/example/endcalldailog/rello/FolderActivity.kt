package com.example.myapplication

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.gallery.FolderAdapter
import com.example.myapplication.gallery.FolderImagesActivity
import com.example.myapplication.gallery.ImageFolder
import java.io.File

class FolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fetchImageFolders()
        } else {
            Log.e("Permission", "Storage permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermission()

    }

    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            fetchImageFolders()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun fetchImageFolders() {
        val folders = getImageFolders(this)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = FolderAdapter(folders) {folder->
            val intent = Intent(this, FolderImagesActivity::class.java)
            intent.putExtra("folder_path", folder.path)
            startActivity(intent)
        }
    }



    fun getImageFolders(context: Context): List<ImageFolder> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.DATA, // deprecated in API 29+, but used for folder path
            MediaStore.Images.Media._ID
        )

        val foldersMap = LinkedHashMap<String, ImageFolder>()

        val cursor = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC" // latest first
        )

        cursor?.use {
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idCol = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val bucketIdCol = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

            while (it.moveToNext()) {
                val folderName = it.getString(nameCol)
                val folderId = it.getString(bucketIdCol)
                val filePath = it.getString(dataCol)
                val imageId = it.getLong(idCol)

                val folderPath = File(filePath).parentFile?.absolutePath ?: continue
                val imageUri = ContentUris.withAppendedId(uri, imageId)

                if (!foldersMap.containsKey(folderId)) {
                    foldersMap[folderId] = ImageFolder(
                        name = folderName,
                        path = folderPath,
                        firstImageUri = imageUri
                    )
                }
            }
        }

        return foldersMap.values.toList()
    }

}