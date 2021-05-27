package com.raindragonn.chapter06_dust.data.model.tmcoodinates


import com.google.gson.annotations.SerializedName

data class TmCoordinatesResponse(
    @SerializedName("documents")
    val documents: List<Document>?,
    @SerializedName("meta")
    val meta: Meta?
)