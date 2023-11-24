package com.egecius.coroutinesdemo

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class FlowExceptions {

    private var count = 0

    private val failingFlow: Flow<Int> = flow {
        count++
        if (count < 3) {
            throw EgisException("from EgisException")
        } else {
            throw RuntimeException("from in RuntimeException")
        }
    }

    @Before
    fun setup() {
        count = 0
    }

    @Test
    fun `'retry' allows retry if a certain condition is met`() = runBlockingTest {
        failingFlow.retry { throwable ->
            // will retry if this condition is met
            throwable is EgisException
        }.test {
            assertThat(expectError().message).isEqualTo("from in RuntimeException")
        }

        // count is 3: 1st as normal try + two as retries
        assertThat(count).isEqualTo(3)
    }

    @Test
    fun `'retry' also allows easily setting the number of retries as  param`() = runBlockingTest {
        failingFlow.retry(retries = 1) { throwable ->
            throwable is EgisException
        }.test {
            assertThat(expectError().message).isEqualTo("from EgisException")
        }

        // count is 2: 1st as normal try + 2nd as retry
        assertThat(count).isEqualTo(2)
    }
}
