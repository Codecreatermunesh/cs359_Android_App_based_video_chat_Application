package com.ashish.videoconferencingtool.models

data class Message(
    val _id: String,
    val message: String? = null,
    val senderId: String,
    val timestamp : Long,
    val file : File? = null
)