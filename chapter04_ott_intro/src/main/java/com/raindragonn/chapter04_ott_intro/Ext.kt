package com.raindragonn.chapter04_ott_intro

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.WindowManager

// Created by raindragonn on 2021/05/25.


fun Int.dpToPx(context: Context): Float =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    )

fun Activity.makeStatusBarTransparent() {
    with(window) {
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
    }
}
