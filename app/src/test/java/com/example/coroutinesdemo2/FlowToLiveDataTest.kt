package com.egecius.coroutinesdemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.egecius.coroutinesdemo.util.MainCoroutineRule
import com.egecius.coroutinesdemo.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FlowToLiveDataTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun `easy to append initial value to flow and covert to live data`() = runBlockingTest {

        val liveData = flowOf(1, 2, 3)
            .onStart { emit(0) }
            .asLiveData()

        val result: Int? = liveData.getOrAwaitValue()

        assertThat(result).isEqualTo(0)
    }
}
