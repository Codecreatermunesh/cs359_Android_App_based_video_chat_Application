package com.munesh.videoconferencingtool.api

import com.munesh.videoconferencingtool.models.request.UserSignInReq
import com.munesh.videoconferencingtool.models.request.UserSignUpReq
import com.munesh.videoconferencingtool.models.response.UserResponse
import com.munesh.videoconferencingtool.utils.Constants.SIGN_IN
import com.munesh.videoconferencingtool.utils.Constants.SIGN_UP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {

    @POST(SIGN_UP)
    suspend fun signUp(@Body userSignUpReq : UserSignUpReq): Response<UserResponse>
    @POST(SIGN_IN)
    suspend fun signIn(@Body userSignInReq : UserSignInReq): Response<UserResponse>
}