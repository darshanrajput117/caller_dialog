package com.example.myapplication.gallery

import android.net.Uri

data class ImageFolder(
    val name: String,
    val path: String,
    val firstImageUri: Uri
)