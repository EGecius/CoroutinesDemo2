package com.egecius.coroutinesdemo

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("EXPERIMENTAL_API_USAGE")
class StateFlowTests {

    @Test
    fun `stateFlow emits last value by default`() = runBlockingTest {
        val stateFlow = MutableStateFlow(8)

        stateFlow.take(1).test {
            expectItem() shouldBe 8
            expectComplete()
        }
    }

    @Test
    fun `stateFlow has inbuilt distinctUntilChanged() functionality which operates on equals() contract`() = runBlockingTest {
        val mutableStateFlow = MutableStateFlow(EgisData(8))
        mutableStateFlow.emit(EgisData(8))
        mutableStateFlow.emit(EgisData(8))

        val resultList = mutableStateFlow.take(1).toList()

        // despite 2 additional items emitted, only 1 emission is received because the emissions were equal to each other
        assertThat(resultList.size).isEqualTo(1)
        assertThat(resultList[0]).isEqualTo(EgisData(8))
    }

    @Test
    fun `only the last emitted value is passed on to a new collector`() = runBlockingTest {
        val mutableStateFlow = MutableStateFlow(EgisData(8))
        mutableStateFlow.emit(EgisData(9))

        mutableStateFlow.take(1).test {
            expectItem() shouldBe EgisData(9)
            expectComplete()
        }
    }
    
    @Test
    fun `read-only value can be created with asStateFlow()`() = runBlockingTest{
        val mutableStateFlow = MutableStateFlow(EgisData(1))
        mutableStateFlow.emit(EgisData(2))

        val readOnlyStateFlow = mutableStateFlow.asStateFlow()
        // emit cannot be called on it
//        readOnlyStateFlow.emit()

        readOnlyStateFlow.take(1).test {
            expectItem() shouldBe EgisData(2)
            expectComplete()
        }
    }
}
