package com.ashish.videoconferencingtool.models.request

data class UserSignInReq(
    val mobile: String,
    val password: String
)