package com.munesh.videoconferencingtool.models

data class Message(
    val _id: String,
    val message: String = "",
    val senderId: String,
    val timestamp : Long,
    val file : MediaFile? = null
)