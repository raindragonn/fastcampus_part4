package com.raindragonn.chapter02_music

// Created by raindragonn on 2021/05/22.

data class MusicModel(
    val id: Long,
    val track: String,
    val streamUrl: String,
    val artist: String,
    val coverUrl: String,
    val isPlaying: Boolean = false
)