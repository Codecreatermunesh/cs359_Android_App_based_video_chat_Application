package com.ashish.videoconferencingtool.models

data class User(
    val _id: String,
    val lastSeen: String,
    val mobile: String,
    val name: String,
    val password: String,
    val status: Boolean
)