package com.raindragonn.chapter05_githubrepo.model.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

// Created by raindragonn on 2021/05/25.

@Entity(tableName = "GithubRepository")
data class GithubRepoEntity(
    @PrimaryKey
    val fullName: String,
    @Embedded
    val owner: GithubOwner,
    val name: String,
    val description: String?,
    val language: String?,
    val updatedAt: String,
    val stargazersCount: Int
)
