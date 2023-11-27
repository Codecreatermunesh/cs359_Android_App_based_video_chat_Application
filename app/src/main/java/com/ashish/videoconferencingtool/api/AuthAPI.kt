package com.ashish.videoconferencingtool.api

import com.ashish.videoconferencingtool.models.request.UserSignInReq
import com.ashish.videoconferencingtool.models.request.UserSignUpReq
import com.ashish.videoconferencingtool.models.response.UserResponse
import com.ashish.videoconferencingtool.utils.Constants.SIGN_IN
import com.ashish.videoconferencingtool.utils.Constants.SIGN_UP
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {

    @POST(SIGN_UP)
    suspend fun signUp(@Body userSignUpReq : UserSignUpReq): Response<UserResponse>
    @POST(SIGN_IN)
    suspend fun signIn(@Body userSignInReq : UserSignInReq): Response<UserResponse>
}