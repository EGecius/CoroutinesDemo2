@file:Suppress("UsePropertyAccessSyntax")

package com.example.coroutinesdemo2

import androidx.lifecycle.viewModelScope
import com.example.coroutinesdemo2.util.MainCoroutineRule
import com.example.coroutinesdemo2.util.failingCoroutine
import com.example.coroutinesdemo2.EgisException
import com.example.coroutinesdemo2.MyViewModel
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import java.net.UnknownHostException
import java.util.concurrent.CancellationException

@ExperimentalCoroutinesApi
class CoroutineExceptionsTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    @Test
    fun `invokeOnCompletion receives exception thrown`() {
        var resultThrowable: Throwable? = null
        CoroutineScope(Dispatchers.Main).launch {
            throw UnknownHostException("egis")
        }.invokeOnCompletion {
            resultThrowable = it
        }

        assertThat(resultThrowable?.message).isEqualTo("egis")
        assertThat(resultThrowable is UnknownHostException).isTrue()
    }

    @Test
    fun `job allows cancelling a coroutine`() {
        var cancelMessage: String? = null
        val job: Job = CoroutineScope(Dispatchers.Main).launch {
            delay(10)
        }
        job.invokeOnCompletion {
            cancelMessage = it?.message
        }

        job.cancel(CancellationException("egis"))

        assertThat(cancelMessage).isEqualTo("egis")
    }

    @Test
    fun `viewModelScope job intercepts exception`() {
        var resultThrowable: Throwable? = null

        MyViewModel().viewModelScope.launch {
            throw Exception("egis")
        }.invokeOnCompletion {
            resultThrowable = it
        }

        assertThat(resultThrowable?.message).isEqualTo("egis")
    }

    @Test
    fun `try catch does catch exceptions thrown by coroutines`() = runTest {

        launch {
            // this will cause a crash
            try {
                delay(1)
                throw EgisException()
            } catch (e: Exception) {
                e shouldBe EgisException()
            }
        }
    }

    @Test
    fun `instead of throwing an exception, a coroutine propagates it up the hierarchy and can be caught with an exception handler installed at the top level`() {
        var isCaughtAtTheTopLevelHandler = false

        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
            println("Handle $exception in CoroutineExceptionHandler")
            isCaughtAtTheTopLevelHandler = true
        }
        val topLevelScope = CoroutineScope(Job() + coroutineExceptionHandler)

        topLevelScope.launch {
            launch {
                throw RuntimeException("RuntimeException in nested coroutine")
            }
        }

        // waiting for the handler body to be executed asynchronously
        Thread.sleep(100)

        assertThat(isCaughtAtTheTopLevelHandler).isTrue()
    }

    @Test
    fun `alternatively, you can pass exception handler as a param to top-level coroutine builder rather than scope`() {
        var isCaughtAtTheTopLevelHandler = false

        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
            println("Handle $exception in CoroutineExceptionHandler")
            isCaughtAtTheTopLevelHandler = true
        }
        val topLevelScope = CoroutineScope(Job())

        // HERE! passing to the coroutine builder rather than topLevelScope
        topLevelScope.launch(coroutineExceptionHandler) {
            launch {
                throw RuntimeException("RuntimeException in nested coroutine")
            }
        }

        // waiting for the handler body to be executed asynchronously
        Thread.sleep(100)

        assertThat(isCaughtAtTheTopLevelHandler).isTrue()
    }
}
