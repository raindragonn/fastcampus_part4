package com.raindragonn.chapter02_music.network

import com.raindragonn.chapter02_music.service.MusicDto
import retrofit2.Call
import retrofit2.http.GET

// Created by raindragonn on 2021/05/22.

interface MusicService {
    @GET("/v3/298e268b-e66f-4f1c-b334-62e75eeca256")
    fun listMusics() : Call<MusicDto>
}