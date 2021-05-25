package com.raindragonn.chapter05_githubrepo.util

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

// Created by raindragonn on 2021/05/25.

class AuthTokenProvider(private val context: Context) {

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun updateToken(token: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(KEY_AUTH_TOKEN, token)
        }
    }

    val token: String?
        get() = PreferenceManager.getDefaultSharedPreferences(context).getString(
            KEY_AUTH_TOKEN, null
        )
}