package com.raindragonn.chapter02_music.service

import com.google.gson.annotations.SerializedName

// Created by raindragonn on 2021/05/22.

data class MusicEntity(
    @SerializedName("track")
    val track: String,
    @SerializedName("streamUrl")
    val streamUrl: String,
    @SerializedName("artist")
    val artist: String,
    @SerializedName("coverUrl")
    val coverUrl: String
)