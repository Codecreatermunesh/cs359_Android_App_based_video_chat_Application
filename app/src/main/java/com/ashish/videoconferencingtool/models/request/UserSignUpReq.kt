package com.ashish.videoconferencingtool.models.request

data class UserSignUpReq(
    val name: String,
    val mobile: String,
    val password: String
)