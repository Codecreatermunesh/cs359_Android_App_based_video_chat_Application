package com.ashish.videoconferencingtool.api

import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.Constants.ALL_USER
import retrofit2.Response
import retrofit2.http.GET

interface UserAPI {

    @GET(ALL_USER)
    @JvmSuppressWildcards
    suspend fun getAllUser() : Response<List<User>>

}