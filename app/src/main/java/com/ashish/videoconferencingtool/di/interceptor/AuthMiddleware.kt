package com.ashish.videoconferencingtool.di.interceptor

import com.ashish.videoconferencingtool.utils.SharedPref
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthMiddleware @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenManager: SharedPref

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        return try {
            val token = tokenManager.getToken()
            request.addHeader("Authorization","Bearer $token")
            chain.proceed(request.build())
        }catch (e: Exception) {
            chain.proceed(request.build())
        }
    }
}