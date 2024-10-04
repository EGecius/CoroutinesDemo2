package com.example.coroutinesdemo2

import app.cash.turbine.Event
import app.cash.turbine.test
import com.example.coroutinesdemo2.util.neverEndingEmptyFlow
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
            awaitItem() shouldBe 7
            awaitItem() shouldBe 9
            awaitComplete()
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `checks if a flow completed`() = runBlocking {
        neverEndingEmptyFlow().test {
            awaitComplete()
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
            awaitEvent() shouldBe Event.Item(67)
            awaitEvent() shouldBe Event.Complete
        }
    }

    @Test(expected = AssertionError::class)
    fun `fails if certain events are not complete`() = runBlockingTest {
        flowOf(1).test {
            awaitEvent() shouldBe 1
        }
    }

    @Test
    fun `flows can be canceled at any time and will not require consuming a complete or error event`() = runBlockingTest {
        flowOf(1, 2, 3, 4).test {
            awaitItem() shouldBe 1
            awaitItem() shouldBe 2
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `allows asserting errors`() = runBlockingTest {
        flow<Unit> { throw RuntimeException("broken!") }.test {
            awaitError().message shouldBe "broken!"
        }
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `times out after 1s`() = runBlockingTest {
        flow<Unit> {
            delay(2000)
        }.test {
            awaitItem() shouldBe "item"
            awaitComplete()
        }
    }

//    @Test(expected = TimeoutCancellationException::class)
//    fun `timeout can be configured`() = runBlockingTest {
//        flow<Unit> {
//            delay(100)
//        }.test(timeout = 50.milliseconds) {
//            awaitItem() shouldBe "item"
//            awaitComplete()
//        }
//    }
}
