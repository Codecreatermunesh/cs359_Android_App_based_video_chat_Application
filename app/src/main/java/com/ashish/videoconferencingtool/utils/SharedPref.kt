package com.ashish.videoconferencingtool.utils

import android.content.Context
import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.ApiConstants.ID
import com.ashish.videoconferencingtool.utils.ApiConstants.NAME
import com.ashish.videoconferencingtool.utils.Constants.PREFS_TOKEN_FILE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPref @Inject constructor(@ApplicationContext context: Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        val editor = prefs.edit()
        editor.putString(ID, user._id)
        editor.putString(NAME, user.name)
        editor.apply()
    }

    fun getUserId() = prefs.getString(ID, null)
    fun deleteUserId() {
        prefs.edit().clear().apply()
    }
    fun getUserName() = prefs.getString(NAME,null)


}