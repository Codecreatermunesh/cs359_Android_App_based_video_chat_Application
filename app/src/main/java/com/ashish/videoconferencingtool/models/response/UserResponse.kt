package com.ashish.videoconferencingtool.models.response

import com.ashish.videoconferencingtool.models.User

data class UserResponse(
    val user : User,
    val token : String
)
