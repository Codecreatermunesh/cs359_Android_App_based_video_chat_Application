package com.munesh.videoconferencingtool.utils

import android.content.Context
import com.munesh.videoconferencingtool.models.User
import com.munesh.videoconferencingtool.utils.Constants.ID
import com.munesh.videoconferencingtool.utils.Constants.PREFS_TOKEN_FILE
import com.munesh.videoconferencingtool.utils.Constants.TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN, token).apply()
    }

    fun getToken() = prefs.getString(TOKEN, null)


//    fun saveFCMToken(fcmToken : String){
//        prefs.edit().putString(FCM_TOKEN, fcmToken).apply()
//    }
//
//    fun getFCMToken() = prefs.getString(FCM_TOKEN, null)

    fun saveUserDetails(user: User) {
        prefs.edit().putString(ID, user.id).apply()
    }

    fun getUserId() = prefs.getString(ID, null)
//    fun deleteUserDetails() {
//        prefs.edit().clear().apply()
//    }


}