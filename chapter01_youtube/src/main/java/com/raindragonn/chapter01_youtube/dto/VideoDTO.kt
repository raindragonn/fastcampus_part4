package com.raindragonn.chapter01_youtube.dto


import com.google.gson.annotations.SerializedName
import com.raindragonn.chapter01_youtube.model.Video

data class VideoDTO(
    @SerializedName("videos")
    val videos: List<Video>
)