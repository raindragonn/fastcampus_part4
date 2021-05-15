package com.raindragonn.chapter01_youtube.network

import com.raindragonn.chapter01_youtube.dto.VideoDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Created by raindragonn on 2021/05/15.

object NetworkManager {
    private const val VIDEO_BASE = "https://run.mocky.io/"
    private val videoRetrofit = Retrofit.Builder()
        .baseUrl(VIDEO_BASE)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val videoClient by lazy { videoRetrofit.create(VideoService::class.java) }

    fun getVideoList(
        response: (Call<VideoDTO>, Response<VideoDTO>) -> Unit,
        failure: (call: Call<VideoDTO>, t: Throwable) -> Unit
    ) {
        videoClient.listVideos().enqueue(
            object : Callback<VideoDTO> {
                override fun onResponse(call: Call<VideoDTO>, response: Response<VideoDTO>) {
                    response(call, response)
                }

                override fun onFailure(call: Call<VideoDTO>, t: Throwable) {
                    failure(call, t)
                }
            }
        )
    }

}