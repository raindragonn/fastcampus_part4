package com.raindragonn.chapter05_githubrepo.model.response

import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity

// Created by raindragonn on 2021/05/26.

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepoEntity>
)
