package com.raindragonn.chapter05_githubrepo.util

import android.content.Context
import android.widget.Toast

// Created by raindragonn on 2021/05/26.


fun Context.toast(msg: String, isLong: Boolean = false) {
    if (isLong) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    } else {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}