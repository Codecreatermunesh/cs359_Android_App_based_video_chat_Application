package com.munesh.videoconferencingtool.models.response

data class LastMessage(
    val _id: String,
    val message: String,
    val `receiver`: String,
    val sender: String,
    val status: Int,
    val timestamp: Long
)