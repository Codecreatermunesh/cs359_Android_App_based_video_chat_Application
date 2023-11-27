package com.ashish.videoconferencingtool.di

import com.ashish.videoconferencingtool.utils.Constants.BASE_URL
import com.ashish.videoconferencingtool.utils.SharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SocketIO {

    @Singleton
    @Provides
    fun providesSocketIO(sharedPref: SharedPref): Socket {
        val options = IO.Options.builder().setAuth(
                mapOf("token" to sharedPref.getToken())
            ).build()
            return IO.socket(BASE_URL, options)
        }
}