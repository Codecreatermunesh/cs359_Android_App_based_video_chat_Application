package com.munesh.videoconferencingtool.models.request

data class UserSignInReq(
    val mobile: Long,
    val password: String
//    val fcmToken : String
)