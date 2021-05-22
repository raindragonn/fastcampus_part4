package com.raindragonn.chapter02_music.service

// Created by raindragonn on 2021/05/22.


// 서버 모델 그자체(순수한 데이터)
data class MusicDto(
    val musics: List<MusicEntity>
)