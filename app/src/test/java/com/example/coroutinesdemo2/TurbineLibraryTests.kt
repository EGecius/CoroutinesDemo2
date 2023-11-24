package com.egecius.coroutinesdemo

import app.cash.turbine.Event
import app.cash.turbine.test
import com.egecius.coroutinesdemo.util.neverEndingEmptyFlow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalCoroutinesApi
@ExperimentalTime
class TurbineLibraryTests {

    @Test
    fun `asserts that certain items were emitted`() = runBlocking {

        flowOf(7, 9).test {
            expectItem() shouldBe 7
            expectItem() shouldBe 9
            expectComplete()
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `checks if a flow completed`() = runBlocking {
        neverEndingEmptyFlow().test {
            expectComplete()
        }
    }

    @Test(expected = AssertionError::class)
    fun `throws assertion error if complete event is found`() = runBlockingTest {
        emptyFlow<Int>().test {
            expectNoEvents()
        }
    }

    @Test
    fun `test passes if no events are found, including no completion event`() = runBlockingTest {
        neverEndingEmptyFlow().test {
            expectNoEvents()
        }
    }

    @Test
    fun `allows to assert events with more detail`() = runBlockingTest {
        flowOf(67).test {
            expectEvent() shouldBe Event.Item(67)
            expectEvent() shouldBe Event.Complete
        }
    }

    @Test(expected = AssertionError::class)
    fun `fails if certain events are not complete`() = runBlockingTest {
        flowOf(1).test {
            expectEvent() shouldBe 1
        }
    }

    @Test
    fun `flows can be canceled at any time and will not require consuming a complete or error event`() = runBlockingTest {
        flowOf(1, 2, 3, 4).test {
            expectItem() shouldBe 1
            expectItem() shouldBe 2
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `allows asserting errors`() = runBlockingTest {
        flow<Unit> { throw RuntimeException("broken!") }.test {
            expectError().message shouldBe "broken!"
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `times out after 1s`() = runBlockingTest {
        flow<Unit> {
            delay(2000)
        }.test {
            expectItem() shouldBe "item"
            expectComplete()
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `timeout can be configured`() = runBlockingTest {
        flow<Unit> {
            delay(100)
        }.test(timeout = 50.milliseconds) {
            expectItem() shouldBe "item"
            expectComplete()
        }
    }
}
