package com.ashish.videoconferencingtool.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("_id")
    val id: String,
    val lastSeen: Long,
    val mobile: String,
    val name: String,
    val password: String? = null,
    var isOnline: Boolean
) : Parcelable