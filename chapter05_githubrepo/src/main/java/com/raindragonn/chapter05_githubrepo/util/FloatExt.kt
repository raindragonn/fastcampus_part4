package com.raindragonn.chapter05_githubrepo.util

import android.content.res.Resources

// Created by raindragonn on 2021/05/26.


fun Float.dpToPx(): Int {
    return (this * Resources.getSystem().displayMetrics.density).toInt()
}
