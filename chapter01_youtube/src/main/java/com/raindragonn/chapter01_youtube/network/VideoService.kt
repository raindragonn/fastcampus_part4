package com.raindragonn.chapter01_youtube.network

import com.raindragonn.chapter01_youtube.dto.VideoDTO
import retrofit2.Call
import retrofit2.http.GET

// Created by raindragonn on 2021/05/15.

interface VideoService {
    @GET("v3/a88ccf93-ed65-45eb-ad23-294f64e80e04")
    fun listVideos(): Call<VideoDTO>
}