package com.raindragonn.chapter06_dust

import android.content.Context
import android.widget.Toast

// Created by raindragonn on 2021/05/26.

internal fun Context.toast(msg: String, isLong: Boolean = false) {
    Toast.makeText(
        this, msg,
        if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}