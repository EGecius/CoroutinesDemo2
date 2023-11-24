package com.egecius.coroutinesdemo

import com.egecius.coroutinesdemo.fakes.FakeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FakeActivityTest {

    @Test
    fun `simulate activity scope activity`() = runBlocking {
        val activity = FakeActivity()
        activity.doSomething() // run test function

        println("Launched coroutines")
        delay(500L) // delay for half a second

        println("Destroying activity!")
        activity.doOnDestroy() // cancels all coroutines
        delay(1000) // visually confirm that they don't work
    }
}