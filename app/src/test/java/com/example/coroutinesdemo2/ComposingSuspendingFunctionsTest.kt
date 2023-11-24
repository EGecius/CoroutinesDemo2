@file:Suppress("MemberVisibilityCanBePrivate")

package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class ComposingSuspendingFunctionsTest {

    @Test
    fun `coroutines execute sequentially by default`() = runBlocking {

        val time = measureTimeMillis {
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            println("The answer is ${one + two}")
        }
        println("Completed in $time ms")
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(100) // pretend we are doing something useful here
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(200) // pretend we are doing something useful here, too
        return 29
    }

    @Test
    fun `async-await executes concurrently`() = runBlocking {

        val time = measureTimeMillis {
            val one = async { doSomethingUsefulOne() }
            val two = async { doSomethingUsefulTwo() }
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    @Test
    fun `async-await allows lazy start`() = runBlocking {
        val time = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            // some computation
            one.start() // start the first one
            two.start() // start the second one

            // if we call await() before start(), then we have sequential execution
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $time ms")
    }

    @Test
    fun `you can turn a suspending function into a normal one with async`() = runBlocking {
        val deferred = somethingUsefulOneAsync()
        val result: Int = deferred.await()
        assertThat(result).isEqualTo(13)
        println("result: $result")
    }

    fun somethingUsefulOneAsync() = GlobalScope.async {
        doSomethingUsefulOne()
    }

    @Test
    fun `failure is always propagated through coroutines hierarchy`() = runBlocking<Unit> {
        try {
            failedConcurrentSum()
        } catch (e: ArithmeticException) {
            println("Computation failed with ArithmeticException")
        }
    }

    suspend fun failedConcurrentSum(): Int = coroutineScope {
        val one = async {
            try {
                delay(Long.MAX_VALUE) // Emulates very long computation
                42
            } finally {
                println("First child was cancelled")
            }
        }
        val two = async<Int> {
            println("Second child throws an exception")
            throw ArithmeticException()
        }
        one.await() + two.await()
    }
}