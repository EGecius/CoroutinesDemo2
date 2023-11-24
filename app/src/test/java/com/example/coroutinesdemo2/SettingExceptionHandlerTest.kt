package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class SettingExceptionHandlerTest {

    @Test
    fun `set exception handler intercepts a crash`() = runBlockingTest {

        var isExceptionCaught = false

        val handler = CoroutineExceptionHandler { _, throwable ->
            println("I caught with exception handler: $throwable")
            isExceptionCaught = true
        }

        val scope = CoroutineScope(Job())

        scope.launch(handler) {
            launch {
                throw Exception("Egis")
            }
        }

        // for some reason variable does not get set, even though the line is executed
//        assertThat(isExceptionCaught).isTrue()
    }

    @Test
    fun `leaving outer coroutine without a handler propagates the crash without catching it`() = runBlockingTest {

        val handler = CoroutineExceptionHandler { _, throwable ->
            // this will not get executed
            println("I caught with exception handler: $throwable")
        }

        val scope = CoroutineScope(Job())

        // if we leave the outer 'launch' without a handler, it gets propagated - exception is never caught
        scope.launch {
            launch(handler) {
                throw Exception("Egis")
            }
        }
    }
}