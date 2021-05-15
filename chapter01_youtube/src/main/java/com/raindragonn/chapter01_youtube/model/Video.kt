package com.raindragonn.chapter01_youtube.model


import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("title")
    val title: String,
    @SerializedName("sources")
    val sources: String,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("thumb")
    val thumb: String,
    @SerializedName("description")
    val description: String
)