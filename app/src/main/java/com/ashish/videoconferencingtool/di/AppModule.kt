package com.ashish.videoconferencingtool.di

import android.app.Application
import com.ashish.videoconferencingtool.utils.NetworkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun provideNetConnection(application: Application): NetworkManager =
        NetworkManager(application)
}