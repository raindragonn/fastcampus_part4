package com.raindragonn.chapter06_dust.data.model.airquality


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val airQualities: List<AirQuality>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)