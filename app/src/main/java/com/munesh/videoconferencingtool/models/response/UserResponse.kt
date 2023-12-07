package com.munesh.videoconferencingtool.models.response

import com.munesh.videoconferencingtool.models.User

data class UserResponse(
    val user : User,
    val token : String
)
