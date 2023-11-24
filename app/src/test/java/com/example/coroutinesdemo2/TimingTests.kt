package com.egecius.coroutinesdemo

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test

class TimingTests {

    @Test
    fun `coAnswers() allows mocking delay time`() = runBlocking {
        val fakeRepo: FakeRepo = mockk(relaxed = true)
        coEvery { fakeRepo.fetchItem() } coAnswers {
            delay(200)
            FakeItem()
        }

        val time0 = System.currentTimeMillis()
        fakeRepo.fetchItem()
        val time1 = System.currentTimeMillis()
        val diff = time1 - time0

        (diff > 200) shouldBe true
        println("diff: $diff")
    }

    @Test
    @Ignore // will never finish
    fun `coAnswers() allows mocking a coroutine that never returns anything`() = runBlocking {
        val fakeRepo: FakeRepo = mockk(relaxed = true)
        coEvery { fakeRepo.fetchItem() } coAnswers {
            while (true) {
                delay(100)
            }
            FakeItem()
        }

        // will never return anything
        fakeRepo.fetchItem()
        Unit
    }
}
