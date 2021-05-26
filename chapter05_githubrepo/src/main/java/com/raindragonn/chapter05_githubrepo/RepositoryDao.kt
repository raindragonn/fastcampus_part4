package com.raindragonn.chapter05_githubrepo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity

// Created by raindragonn on 2021/05/25.

@Dao
interface RepositoryDao {
    @Insert
    suspend fun insert(repo: GithubRepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repoList: List<GithubRepoEntity>)

    @Query("select * from githubrepository")
    suspend fun getHistories(): List<GithubRepoEntity>

    @Query("select * from githubrepository where fullName = :fullName")
    suspend fun getHistory(fullName: String): GithubRepoEntity?


    @Query("delete from githubrepository where fullName = :repoName")
    suspend fun remove(repoName: String)

    @Query("delete from githubrepository")
    suspend fun clearAll()
}