package com.munesh.videoconferencingtool.api

import com.munesh.videoconferencingtool.models.Message
import com.munesh.videoconferencingtool.utils.Constants.CHAT
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChatAPI {
    @POST(CHAT)
    @Headers("Cache-Control:no-cache")
    suspend fun getUserChat(@Body participants : List<String>):Response<List<Message>>

    @Multipart
    @POST("chat/file")
    suspend fun uploadFile(@Part file : MultipartBody.Part, @Part receiverIdBody : MultipartBody.Part) : Response<Message>
}