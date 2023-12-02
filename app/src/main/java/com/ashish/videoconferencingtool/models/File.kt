package com.ashish.videoconferencingtool.models

data class File(
    val fileType: String,
    val originalname: String,
    val size: Int,
    val url: String
)