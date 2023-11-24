package com.egecius.coroutinesdemo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FlowExceptionTest {

    private val flow: Flow<Int> = flow {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i) // emit next value
        }
    }

    @Test
    fun `you can catch exceptions thrown in collect block`() = runBlocking {
        try {
            flow.collect {
                println(it)
                check(it <= 1) { "Collected $it" }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `adding 'catch' operator allows avoiding full try-catch block`() = runBlocking {
        flow {
            emit(1)
            throw IllegalStateException()
        }
            .catch { e -> println(e) }
            .collect()
    }
} 