package com.ashish.videoconferencingtool.utils

object Constants {

    // Base Url
    const val BASE_URL = "http://192.168.37.130:3000/"

    const val TAG = "ChatApp"

    // Constants
    const val PREFS_TOKEN_FILE = "foozzy_local_db"

    const val MSG = "message"

    // Messages
    const val NO_INTERNET_CONNECTION = "No Internet Connection"
    const val SOMETHING_WENT_WRONG = "Something Went Wrong"

    // Variables
    const val ID = "id"
//    const val USER_ID = "userId"
    const val IS_ONLINE = "isOnline"
    const val LAST_SEEN = "lastSeen"
//    const val STATUS = "status"
//    const val NAME = "name"
//    const val MOBILE = "mobile"
//    const val PASSWORD = "password"

    const val TOKEN = "token"
//    const val FCM_TOKEN = "fcm_token"

    // Socket IO Endpoints
//    const val USER_CONNECTION = "user-connection"

    const val USER_ONLINE_STATUS = "userOnlineStatus"
    const val _CHAT = "chat"
    const val CHAT_SUCCESS = "chat-success"

    // API Endpoints
    const val SIGN_UP = "auth/signup"
    const val SIGN_IN = "auth/signin"
    const val ALL_USER = "user/all"
    const val CHAT = "chat/"

    // Constants
    private const val MINUTE = 60 * 1000
    private const val HOUR = 60 * MINUTE
    const val DAY = 24 * HOUR


}