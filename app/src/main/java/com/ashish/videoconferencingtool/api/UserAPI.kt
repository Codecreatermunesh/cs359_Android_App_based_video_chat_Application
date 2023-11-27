package com.ashish.videoconferencingtool.api

import com.ashish.videoconferencingtool.models.User
import com.ashish.videoconferencingtool.utils.Constants.ALL_USER
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST(ALL_USER)
    @JvmSuppressWildcards
    suspend fun getAllUser(@Body contacts :Map<String, List<Long>>) : Response<List<User>>

}