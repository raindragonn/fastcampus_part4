package com.raindragonn.chapter06_imagesearch.data.service

import com.raindragonn.chapter06_imagesearch.BuildConfig
import com.raindragonn.chapter06_imagesearch.data.model.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Created by raindragonn on 2021/05/27.

interface UnsplashApiService {

    @GET(
        "photos/random?" +
                "client_id=${BuildConfig.UNSPLASH_ACCESS_KEY}" +
                "&count=30"
    )

    suspend fun getRandomPhotos(
        @Query("query") query: String?
    ): Response<List<PhotoResponse>>
}