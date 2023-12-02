package com.ashish.videoconferencingtool.di

import com.ashish.videoconferencingtool.api.AuthAPI
import com.ashish.videoconferencingtool.api.ChatAPI
import com.ashish.videoconferencingtool.api.UserAPI
import com.ashish.videoconferencingtool.di.interceptor.AuthMiddleware
import com.ashish.videoconferencingtool.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    private val interceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(interceptor).build()

    @Singleton
    @Provides
    fun providesRetrofit() : Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .baseUrl(BASE_URL)
            .client(okHttpClient)
    }

    @Singleton
    @Provides
    fun provideOkHttpOwnerAuth(ownerAuth: AuthMiddleware): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(ownerAuth)
            .build()
    }
    @Singleton
    @Provides
    fun providesAuthAPI(retrofit: Retrofit.Builder): AuthAPI{
        return retrofit.build().create(AuthAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesUserAPI(retrofit: Retrofit.Builder,okHttpClient: OkHttpClient): UserAPI {
        return retrofit.client(okHttpClient).build().create(UserAPI::class.java)
    }

    @Singleton
    @Provides
    fun providesChatAPI(retrofit: Retrofit.Builder,okHttpClient: OkHttpClient): ChatAPI {
        return retrofit.client(okHttpClient).build().create(ChatAPI::class.java)
    }
}