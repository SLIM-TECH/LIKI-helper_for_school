package com.ciberssh.liki.data.models

import android.graphics.Bitmap

data class Book(
    val title: String,
    val fileName: String,
    val filePath: String,
    val subject: String = "",
    val pageCount: Int = 0,
    val coverImage: Bitmap? = null,
    val isDownloaded: Boolean = true
)
