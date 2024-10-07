package com.example.coroutinesdemo2

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MapFunctionComparisonTests {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `mapLatest cancels transformation, once new value is by the original flow`() = runTest {
        val flow = flow {
            emit(1)
            delay(100)
            emit(2)
        }

        flow.mapLatest {
            delay(101)
            it * 3
        }.toList() shouldBe listOf(6)
    }
}
