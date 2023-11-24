package com.egecius.coroutinesdemo

import app.cash.turbine.test
import com.egecius.coroutinesdemo.util.neverEndingEmittingFlow
import com.egecius.coroutinesdemo.util.neverEndingEmptyFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Ignore
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class FlowNeverEndingTest {

    @Test
    @Ignore("will never complete")
    fun `never ending empty flow will block the test`() = runBlockingTest {
        // this will never complete & block the test
        neverEndingEmptyFlow().collect { }
    }

    @Test
    fun `a never ending emitting flow can be terminated by using 'take'`() = runBlockingTest {

        neverEndingEmittingFlow().take(1).collect { }
    }

    @Test
    fun `cancelling never-ending flow allows test to finish`() = runBlockingTest {

        val job = launch {
            neverEndingEmptyFlow().collect { }
        }
        job.cancel()
    }

    @Test
    fun `Turbine library verify that no assertions were made`() = runBlockingTest {
        neverEndingEmptyFlow().test { expectNoEvents() }
    }

    @Test (expected = AssertionError::class)
    fun `Turbine library catches emissions`() = runBlockingTest {
    	flowOf(1).test { expectNoEvents() }
    }
}
