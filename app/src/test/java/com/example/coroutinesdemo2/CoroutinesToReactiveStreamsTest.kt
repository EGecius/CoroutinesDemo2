package com.egecius.coroutinesdemo

import io.reactivex.subscribers.TestSubscriber
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.reactivestreams.Publisher

@ExperimentalCoroutinesApi
class CoroutinesToReactiveStreamsTest {

    @Test
    fun `converts flow to reactive streams`() = runBlockingTest {
        val publisher: Publisher<Int> = getFlow().asPublisher()

        val testSubscriber = TestSubscriber<Int>()
        publisher.subscribe(testSubscriber)

        testSubscriber.assertResult(1, 2, 3)
    }

    private fun getFlow() = flowOf(1, 2, 3)
}