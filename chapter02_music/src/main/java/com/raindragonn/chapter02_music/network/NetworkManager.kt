package com.raindragonn.chapter02_music.network

import com.raindragonn.chapter02_music.service.MusicDto
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

// Created by raindragonn on 2021/05/22.

object NetworkManager {
    private const val MUSIC_BASE_URL = "https://run.mocky.io"

    private val musicRetrofit =
        Retrofit.Builder().baseUrl(MUSIC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()

    private val musicService = musicRetrofit.create(MusicService::class.java)

    fun getList(
        response: (call: Call<MusicDto>, response: Response<MusicDto>) -> Unit,
        failure: (call: Call<MusicDto>, t: Throwable) -> Unit
    ) {
        musicService.listMusics().enqueue(object : Callback<MusicDto> {
            override fun onResponse(call: Call<MusicDto>, response: Response<MusicDto>) {
                response(call, response)
            }

            override fun onFailure(call: Call<MusicDto>, t: Throwable) {
                failure(call, t)
            }
        })
    }
}