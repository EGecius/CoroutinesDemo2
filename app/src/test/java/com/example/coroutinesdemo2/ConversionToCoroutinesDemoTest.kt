package com.egecius.coroutinesdemo

import com.egecius.coroutinesdemo.fakes.FakeCall
import com.egecius.coroutinesdemo.fakes.FakeRetrofitService
import com.egecius.coroutinesdemo.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ConversionToCoroutinesDemoTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private val fakeRetrofitService = FakeRetrofitService()

    @Test
    fun `shows how converting Retrofit's API to Coroutine works`() = runBlockingTest {

        val result = fakeRetrofitService.getDataAsCoroutine()

        assertThat(result).isEqualTo(FakeCall.FAKE_RESULT)
    }
}
