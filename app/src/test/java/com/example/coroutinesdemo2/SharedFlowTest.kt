package com.egecius.coroutinesdemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.egecius.coroutinesdemo.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class SharedFlowTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `shared flow can be tested by converting to cold flow which will complete`() = runBlockingTest {

        val mutableSharedFlow = MutableSharedFlow<String>(replay = 1)
        mutableSharedFlow.emit("event-1")

        // convert it it to a cold flow which unlike SharedFlow will complete
        val coldFlow: Flow<String> = mutableSharedFlow.take(1)

        coldFlow.test {
            expectItem() shouldBe "event-1"
            expectComplete()
        }
    }
}
