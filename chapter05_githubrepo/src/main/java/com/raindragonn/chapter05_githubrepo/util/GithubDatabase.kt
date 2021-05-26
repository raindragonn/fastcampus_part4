package com.raindragonn.chapter05_githubrepo.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raindragonn.chapter05_githubrepo.RepositoryDao
import com.raindragonn.chapter05_githubrepo.model.room.GithubRepoEntity

// Created by raindragonn on 2021/05/25.

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class GithubDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        private const val DB_NAME = "github_repository_DB"
        private var mInstance: GithubDatabase? = null

        fun getInstance(context: Context): GithubDatabase {
            return mInstance ?: synchronized(GithubDatabase::class) {
                Room.databaseBuilder(
                    context,
                    GithubDatabase::class.java,
                    DB_NAME
                ).build()
                    .also { db ->
                        mInstance = db
                    }
            }
        }
    }

}