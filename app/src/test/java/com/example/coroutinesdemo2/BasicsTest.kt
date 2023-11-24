package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class BasicsTest {

    @Test
    fun `runBlocking blocks the thread until it completes`() {

        runBlocking {
            println("before delay()")
            delay(100)
            println("after delay()")
        }

        println("end of test")
    }

    @Test
    fun `having 100k coroutines complete takes less than 1s`() = runBlocking {
        val deferredList: List<Deferred<Int>> = (1..1000_000).map { n ->
            GlobalScope.async {
                n
            }
        }

        val sum = deferredList.map { it.await().toLong() }.sum()

        println(sum)
    }

    @Test
    fun `coroutines run in parallel`() = runBlocking {
        (1..100_000).map { n ->
            GlobalScope.async {
                // if they would be queued it would take 10k seconds(1+ day) (0.1sx100k=10k)
                delay(100)
                n
            }
        }
        println("yes, they run in parallel")
    }

    @Test
    fun `shows how to use scope builder`() = runBlocking { // this: CoroutineScope
        launch {
            delay(200L)
            println("Task from runBlocking")
        }

        coroutineScope { // Creates a coroutine scope
            launch {
                delay(500L)
                println("Task from nested launch")
            }

            delay(100L)
            println("Task from coroutine scope") // This line will be printed before the nested launch
        }

        println("Coroutine scope is over") // This line is not printed until the nested launch completes
    }
}