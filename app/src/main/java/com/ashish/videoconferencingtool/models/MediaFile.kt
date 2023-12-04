package com.ashish.videoconferencingtool.models


data class MediaFile(
    val fileType: String,
    val originalName: String,
    val size: Long,
    var url: String,
    val mimeType: String
)