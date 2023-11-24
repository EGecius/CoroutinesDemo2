@file:Suppress("UsePropertyAccessSyntax")

package com.egecius.coroutinesdemo

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.*
import org.junit.Test

@ExperimentalCoroutinesApi
class JoinDemoTest {

    @Test
    fun `join() pauses execution until entire coroutine is finished`() = runBlockingTest {
        val job: Job = launch {
            delay(100)
        }

        job.join()
        assertThat(job.isCompleted).isTrue()
    }

    @Test
    fun `without calling join() you don't wait until entire coroutine is finished`() = runBlockingTest {
        val job: Job = launch {
            delay(100)
        }

//        job.join()
        assertThat(job.isCompleted).isFalse()
    }

    @Test
    fun `it's safe to call join() even after a coroutine is cancelled`() = runBlockingTest {
        val job: Job = launch {
            delay(100)
        }

        job.cancel()
        job.join()
        assertThat(job.isCompleted).isTrue()
        assertThat(job.isCancelled).isTrue()
    }
}