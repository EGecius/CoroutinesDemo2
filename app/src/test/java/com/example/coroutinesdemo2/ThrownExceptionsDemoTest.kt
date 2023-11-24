@file:Suppress("IMPLICIT_NOTHING_AS_TYPE_PARAMETER")

package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
class ThrownExceptionsDemoTest {

    @Test
    fun `within launch() exception gets thrown immediately`() = runBlockingTest {

        var isExceptionCaught = false

        launch {
            try {
                throw Exception("egis")
            } catch (exception: Exception) {
                print("exception caught in my try/catch block:: $exception")
                isExceptionCaught = true
            }
        }

        assertThat(isExceptionCaught).isTrue()
    }

    @Test
    fun `within async() exception gets thrown only when you call await()`() = runBlockingTest {

        var isExceptionCaught = false

        val deferred = async {
            throw Exception("egis")
        }

        assertThat(isExceptionCaught).isFalse()
        // only now exception will be thrown
        println("calling await()")

        try {
            deferred.await()
        } catch (exception: Exception) {
            print("exception caught in my try/catch block:: $exception")
            isExceptionCaught = true
        }

        assertThat(isExceptionCaught).isTrue()
    }

    @Test
    fun `only supervisor scope gets access to thrown exception`() = runBlockingTest {

        var isExceptionCaught = false

        supervisorScope {

            val deferred = async {
                throw Exception("Egis")
            }

            try {
                deferred.await()
            } catch (e: Exception) {
                isExceptionCaught = true
                println("my exception caught: $e")
            }
        }

        assertThat(isExceptionCaught).isTrue()
    }

    @Test(expected = Exception::class)
    fun `non-supervisor scope does not get access to thrown exception`() = runBlockingTest {

        var isExceptionCaught = false

        coroutineScope {

            val deferred = async {
                throw Exception("Egis")
            }

            try {
                deferred.await()
            } catch (e: Exception) {
                isExceptionCaught = true
                println("my exception caught: $e")
            }
        }

        assertThat(isExceptionCaught).isFalse()
    }

    @Test
    fun `runCatching catches exceptions`() = runBlockingTest {

        supervisorScope {

            val deferred = async {
                throw Exception("Egis")
            }

            val result = kotlin.runCatching {
                deferred.await()
            }
            assertThat(result.isFailure).isTrue()
        }
    }
}