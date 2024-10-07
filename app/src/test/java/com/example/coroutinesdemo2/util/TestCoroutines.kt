package com.example.coroutinesdemo2.util

import com.example.coroutinesdemo2.EgisException
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


suspend fun nonFailingCoroutine() {
    delay(10)
    print("nonFailingCoroutine")
}
