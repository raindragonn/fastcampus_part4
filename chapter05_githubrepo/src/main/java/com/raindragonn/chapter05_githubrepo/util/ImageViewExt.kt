package com.raindragonn.chapter05_githubrepo.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

// Created by raindragonn on 2021/05/26.


private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

internal fun ImageView.clear() = Glide.with(context).clear(this)

internal fun ImageView.loadCenterInside(url: String, corner: Float = 0f) {
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade(factory))
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .apply {
            if (corner > 0) transform(CenterInside(), RoundedCorners(corner.dpToPx()))
        }
        .into(this)
}