package com.egecius.coroutinesdemo.mutex

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Test
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class MutexDemo {

    private val mutex = Mutex()
    private var counter = 0

    @Test
    fun `with mutex incrementation is done correctly`() = runBlocking {

        withContext(Dispatchers.Default) {
            run1kTimes {
                // protect each increment with lock
                mutex.withLock {
                    counter++
                }
            }
        }
        println("Counter = $counter")

        counter shouldBe 1000
    }

    @Test
    fun `without mutex incrementation will not work correctly`() = runBlocking {
        withContext(Dispatchers.Default) {
            run1kTimes {
                counter++
            }
            println("Counter = $counter")
        }
        counter shouldBe 1000
    }

    @Test
    fun `using 2 locks produces a deadlock - the test will never finish`() = runBlocking {
        mutex.withLock {
            mutex.withLock {
            }
        }
    }
}

suspend fun run1kTimes(action: suspend () -> Unit) {
    val noOfCoroutines = 10  // number of coroutines to launch
    val noOfActions = 100 // times an action is repeated by each coroutine
    val time = measureTimeMillis {
        coroutineScope { // scope for coroutines
            repeat(noOfCoroutines) {
                launch {
                    repeat(noOfActions) { action() }
                }
            }
        }
    }
    println("Completed ${noOfCoroutines * noOfActions} actions in $time ms")
}
