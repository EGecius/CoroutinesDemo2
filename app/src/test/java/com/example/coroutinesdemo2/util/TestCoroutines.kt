package com.egecius.coroutinesdemo.util

import android.util.Log
import com.egecius.coroutinesdemo.EgisException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun neverEndingEmptyFlow(): Flow<Unit> {
    return flow {
        while (true) {
            delay(100)
        }
    }
}

fun neverEndingEmittingFlow(): Flow<Unit> {
    return flow {
        while (true) {
            delay(10)
            emit(Unit)
        }
    }
}

suspend fun failingCoroutine() {
    delay(1)
    throw EgisException()
}


suspend fun nonFailingCoroutine() {
    delay(10)
    print("nonFailingCoroutine")
}

