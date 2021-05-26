package com.raindragonn.chapter05_githubrepo.network

import com.raindragonn.chapter05_githubrepo.model.response.GithubAccessTokenResponse
import retrofit2.Response
import retrofit2.http.*

// Created by raindragonn on 2021/05/25.

interface GithubAuthService {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Response<GithubAccessTokenResponse>
}