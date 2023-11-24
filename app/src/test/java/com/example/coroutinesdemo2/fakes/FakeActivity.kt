package com.egecius.coroutinesdemo.fakes

import kotlinx.coroutines.*


/** Demonstrates the broad principles of how implement scope in an activity */
class FakeActivity
    : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    // all you have tod is cancel onDestroy:

    fun doOnDestroy() {
        cancel() // Extension on CoroutineScope
    }

    fun doSomething() {
        // launch ten coroutines for a demo, each working for a different time
        repeat(10) { i ->
            launch {
                delay((i + 1) * 200L) // variable delay 200ms, 400ms, ... etc
                println("Coroutine $i is done")
            }
        }
    }
}