package com.raindragonn.chapter03_map.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Created by raindragonn on 2021/05/23.

@Parcelize
data class LocationLatLngEntity(
    val latitude: Float,
    val longitude: Float
) : Parcelable
