package com.ashish.videoconferencingtool.models.request

data class UserSignUpReq(
    val name: String,
    val mobile: Long,
    val password: String
//    val fcmToken : String
)