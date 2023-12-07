package com.munesh.videoconferencingtool.api

import com.munesh.videoconferencingtool.models.User
import com.munesh.videoconferencingtool.utils.Constants.ALL_USER
import retrofit2.Response
import retrofit2.http.GET

interface UserAPI {

    @GET(ALL_USER)
    @JvmSuppressWildcards
    suspend fun getAllUser() : Response<List<User>>

}