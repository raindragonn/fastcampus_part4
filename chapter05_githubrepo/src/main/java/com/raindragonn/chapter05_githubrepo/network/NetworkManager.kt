package com.raindragonn.chapter05_githubrepo.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.raindragonn.chapter05_githubrepo.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Created by raindragonn on 2021/05/25.

object NetworkManager {
    private const val TIME_OUT_SEC = 5L
    private const val BASE_GITHUB_URL = "https://github.com"
    private const val BASE_GITHUB_API_URL = "https://api.github.com"

    val authApiService: GithubAuthApiService by lazy {
        getGithubRetrofit().create(
            GithubAuthApiService::class.java
        )
    }

    private fun getGithubRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_GITHUB_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(buildOkHttpClient())
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }
}