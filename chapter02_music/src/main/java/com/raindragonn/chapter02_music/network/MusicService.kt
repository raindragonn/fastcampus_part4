package com.raindragonn.chapter02_music.network

import com.raindragonn.chapter02_music.service.MusicDto
import retrofit2.Call
import retrofit2.http.GET

// Created by raindragonn on 2021/05/22.

interface MusicService {
    @GET("/v3/99733967-49ed-439a-85e6-6798c95f69e8")
    fun listMusics() : Call<MusicDto>
}