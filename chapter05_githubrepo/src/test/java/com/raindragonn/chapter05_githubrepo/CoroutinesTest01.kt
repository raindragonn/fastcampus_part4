package com.raindragonn.chapter05_githubrepo

import kotlinx.coroutines.Delay
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

// Created by raindragonn on 2021/05/25.

class CoroutinesTest01 {
    @Test
    fun test01() = runBlocking {
        val time = measureTimeMillis {
            val name = getFistName()
            val lastName = getLastName()
            println("Hello, $name $lastName")
        }
        println("measureTIme = : $time")
    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis {
            val name = async { getFistName() }
            val lastName = async { getLastName() }
            println("Hello, ${name.await()} ${lastName.await()}")
        }
        println("measureTIme = : $time")
    }

    suspend fun getFistName(): String {
        delay(1000)
        return "Blue"
    }

    suspend fun getLastName(): String {
        delay(1000)
        return "Pig"
    }
}