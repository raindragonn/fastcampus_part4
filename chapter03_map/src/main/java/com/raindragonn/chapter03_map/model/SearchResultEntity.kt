package com.raindragonn.chapter03_map.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Created by raindragonn on 2021/05/23.

@Parcelize
data class SearchResultEntity(
    val fullAddress: String,
    val name: String,
    val locationLatLng: LocationLatLngEntity
) : Parcelable
