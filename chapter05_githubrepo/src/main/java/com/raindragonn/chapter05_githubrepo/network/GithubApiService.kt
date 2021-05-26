package com.raindragonn.chapter05_githubrepo.network

import com.raindragonn.chapter05_githubrepo.model.response.GithubAccessTokenResponse
import com.raindragonn.chapter05_githubrepo.model.response.GithubRepoSearchResponse
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity
import retrofit2.Response
import retrofit2.http.*

// Created by raindragonn on 2021/05/25.

interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q")
        query: String
    ): Response<GithubRepoSearchResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner")
        ownerLogin: String,
        @Path("name")
        repoName: String
    ): Response<GithubRepoEntity>
}