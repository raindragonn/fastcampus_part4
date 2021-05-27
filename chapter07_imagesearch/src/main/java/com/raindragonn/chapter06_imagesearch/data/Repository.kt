package com.raindragonn.chapter06_imagesearch.data

import com.raindragonn.chapter06_imagesearch.BuildConfig
import com.raindragonn.chapter06_imagesearch.data.model.PhotoResponse
import com.raindragonn.chapter06_imagesearch.data.service.UnsplashApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

// Created by raindragonn on 2021/05/27.

object Repository {
    private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
    private const val TIMEOUT_SEC = 5L


    suspend fun getRandomPhotos(query: String?): List<PhotoResponse>? =
        unSplashApiService.getRandomPhotos(query)
            .body()

    private val unSplashApiService: UnsplashApiService by lazy {
        Retrofit.Builder()
            .baseUrl(UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
            .create()
    }

    private fun buildOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SEC, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()

}