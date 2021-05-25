package com.raindragonn.chapter05_githubrepo.model.response

// Created by raindragonn on 2021/05/25.

data class GithubAccessTokenResponse(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)
