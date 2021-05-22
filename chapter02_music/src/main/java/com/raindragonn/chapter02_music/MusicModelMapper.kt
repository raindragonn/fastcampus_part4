package com.raindragonn.chapter02_music

import com.raindragonn.chapter02_music.service.MusicDto
import com.raindragonn.chapter02_music.service.MusicEntity

// Created by raindragonn on 2021/05/22.

fun MusicEntity.mapper(id: Long): MusicModel {
    return MusicModel(
        id = id,
        streamUrl = streamUrl,
        coverUrl = coverUrl,
        track = track,
        artist = artist
    )
}

fun MusicDto.mapper(): PlayerModel {
    return PlayerModel(
        playMusicList = musics.mapIndexed { index, musicEntity ->
            musicEntity.mapper(index.toLong())
        }
    )
}