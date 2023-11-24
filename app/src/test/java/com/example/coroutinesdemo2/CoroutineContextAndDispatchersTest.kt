package com.egecius.coroutinesdemo

import android.util.Log
import com.egecius.coroutinesdemo.util.log
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class CoroutineContextAndDispatchersTest {

    @Ignore // TODO: 09/10/2020 fix failing test
    @Test
    fun `coroutine inherits context, including dispatcher, from the scope it's launched from`() = runBlocking {

        launch {
            val name = Thread.currentThread().name
            // since runBlocking runs on main thread, main thread will be printed
            print(name)
            assertThat(name).contains("main")
        }
        Unit
    }

    @Test
    fun `coroutine started with its own IO dispatcher does not inherit a dispatcher from parent`() = runBlocking {
        launch(IO) {
            val name = Thread.currentThread().name
            // since runBlocking runs on main thread, main thread will be printed
            print(name)
            assertThat(name).contains("worker")
        }
        Unit
    }

    @Ignore // TODO: 09/10/2020 fix failing test
    @Test
    fun `you can run in your own thread`() = runBlockingTest {
        @Suppress("EXPERIMENTAL_API_USAGE")
        // A dedicated thread is a very expensive resource, so would have to be used with special care
        launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
            println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `you can run in unconfined Dispatcher`() = runBlockingTest {
        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `unconfined coroutines are fully determined by the caller thread`() = runBlocking<Unit> {
        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
            delay(50)
            // now  unconfined one resumes in the default executor thread that the delay function is using
            println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
        }
        launch { // context of the parent, main runBlocking coroutine
            println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(200)
            println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `debug coroutines`() = runBlocking {
        val a = async {
            println("1 child: $coroutineContext")
            6
        }
        val b = async {
            println(coroutineContext)
            println("2 child: $coroutineContext")
            7
        }
        val c = async {
            println(coroutineContext)
            println("3 child: $coroutineContext")
            2
        }
        println("Main: The answer is ${a.await() * b.await() * c.await()} in $coroutineContext")
    }

    @Test
    fun `withContext() changes context but keeps you in the same coroutine`() = runBlocking {
        newSingleThreadContext("Ctx1").use { ctx1 ->
            newSingleThreadContext("Ctx2").use { ctx2 ->

                runBlocking(ctx1) {
                    log("Started in ctx1")
                    withContext(ctx2) {
                        log("Working in ctx2")
                    }
                    log("Back to ctx1")
                }
            }
        }
    }

    @Test
    fun `withContext() allows following the convention that all suspend functions should be non-blocking`() = runBlocking {
        val result: BigInteger = findBigPrime()

        assertThat(result).isNotNull

        Unit
    }

    private suspend fun findBigPrime(): BigInteger {
        val name = Thread.currentThread().name
        println("findBigPrime() outside withContext() thread: $name")
        return withContext(Dispatchers.Default) {
            val name2 = Thread.currentThread().name
            println("findBigPrime() within Dispatchers.Default thread: $name2")
            BigInteger.probablePrime(2048, Random())
        }
    }

    @Test
    fun `you can print coroutine job`() = runBlocking {
        println("My job is ${coroutineContext[Job]}")
    }

    @Test
    fun `coroutine started from global scope does not get cancelled when outer coroutine is cancelled`() = runBlocking {

        // launch a coroutine to process some kind of incoming request
        val request = launch {
            // it spawns two other jobs, one with GlobalScope
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            // and the other inherits the parent context
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel() // cancel processing of the request
        delay(1000) // delay a second to see what happens
        println("main: Who has survived request cancellation?")
    }

    @Test
    fun `a parent coroutine always waits for completion of all its children`() = runBlocking {
        // launch a coroutine to process some kind of incoming request
        val request = launch {
            repeat(3) { i -> // launch a few children jobs
                launch {
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    println("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
        request.join() // wait for completion of the request, including all its children
        println("Now processing of the request is complete")
    }

    @Test
    fun `print coroutine name`() = runBlocking<Unit> {
        launch(CoroutineName("egis-name")) {
            val coroutineName: CoroutineName? = coroutineContext[CoroutineName]
            println(coroutineName)
            assertThat(coroutineName!!.name).isEqualTo("egis-name")
        }
    }

    @Test
    fun `it's useful to give names to coroutines for debugging`() = runBlocking {
        log("Started main coroutine")
        // run two background value computations
        val v1 = async(CoroutineName("v1coroutine")) {
            delay(500)
            log("Computing v1")
            252
        }
        val v2 = async(CoroutineName("v2coroutine")) {
            delay(1000)
            log("Computing v2")
            6
        }
        log("The answer for v1 / v2 = ${v1.await() / v2.await()}")
    }

    @Test
    fun `you can pass multiple context elements at the same time`() = runBlocking<Unit> {
        val context: CoroutineContext = Dispatchers.Default + CoroutineName("egis-test")
        println("context: $context")
        launch(context) {
            val threadName = Thread.currentThread().name
            println("thread: $threadName")
            assertThat(threadName).contains("egis-test")
        }
    }

    @Test
    fun `calling await multiple times returns same result each time`() = runBlockingTest {

        val async = async {
            getValueFromCoroutine()
        }

        async.await() shouldBe 1
        async.await() shouldBe 1
        async.await() shouldBe 1
    }

    private suspend fun getValueFromCoroutine(): Int {
        delay(1)
        return 1
    }
}
