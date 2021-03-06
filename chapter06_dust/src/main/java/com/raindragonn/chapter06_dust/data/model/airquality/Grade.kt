package com.raindragonn.chapter06_dust.data.model.airquality

import androidx.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import com.raindragonn.chapter06_dust.R

// Created by raindragonn on 2021/05/27.

enum class Grade(
    val label: String,
    val emoji: String,
    @ColorRes val colorResId: Int
) {
    @SerializedName("1")
    GOOD("μ’μ", "π", R.color.blue),

    @SerializedName("2")
    NORMAL("λ³΄ν΅", "βΊοΈ", R.color.green),

    @SerializedName("3")
    BAD("λμ¨", "π", R.color.yellow),

    @SerializedName("4")
    AWFUL("λ§€μ° λμ¨", "π‘", R.color.red),

    UNKWON("λ―ΈμΈ‘μ ", "π§", R.color.gray);

    override fun toString(): String {
        return "$label $emoji"
    }
}