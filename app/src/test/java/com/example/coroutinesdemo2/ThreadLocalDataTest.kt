package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import org.junit.Test

@ExperimentalCoroutinesApi
class ThreadLocalDataTest {

    @Test
    fun `show how to have Thread-Local data`() = runBlocking {

        val threadLocal = ThreadLocal<String?>() // declare thread-local variable
        threadLocal.set("main")

        println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")

        val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
            println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            yield()
            println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }
        job.join()
        println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
    }
}