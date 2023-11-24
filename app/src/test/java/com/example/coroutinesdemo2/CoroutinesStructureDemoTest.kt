@file:Suppress("UsePropertyAccessSyntax")

package com.egecius.coroutinesdemo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.egecius.coroutinesdemo.util.MainCoroutineRule
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.lang.Thread.sleep
import java.net.UnknownHostException
import java.util.concurrent.CancellationException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CoroutinesStructureDemoTest {

    private var sut: AsyncAwaitActivity? = null

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        sut = AsyncAwaitActivity()
    }


    @Test
    fun `job's isComplete property returns true after it finishes`() = runBlockingTest {
        val job: Job = launch {
            delay(10)
        }

        job.join()
        assertThat(job.isCompleted).isTrue()
    }


    @Test
    fun `job's isCancelled & isComplete properties returns true after it's cancelled`() = runBlockingTest {
        val job: Job = launch {
            delay(10)
        }

        job.cancel()
        assertThat(job.isCancelled).isTrue()
        assertThat(job.isCompleted).isTrue()
    }

    @Test
    fun `different contexts are printed for parent & child`() = runBlockingTest {

        // this talk suggested that by default a new coroutine inherits parent context but logs suggest otherwise:
        // https://youtu.be/w0kfnydnFWI?t=290

        launch {

            print("parent context:\n")
            print(coroutineContext)

            launch {

                print(":\nchild context:\n")
                print(coroutineContext)


            }
        }
    }

    @Test
    fun `cancelling parent scope cancels all children`() = runBlockingTest {

        var hasRunChild1 = false
        var hasRunChild2 = false
        launch {

            launch {
                print("\nstarting child1:")
                hasRunChild1 = true
                print("\ncompleted child1")
            }

            launch {
                print("\nstarting child2:")
                // this one has longer delay, so should not be run
                delay(300)

                hasRunChild2 = true
                print("\ncompleted child2")
            }

            // cancelling parent scope will cancel children that have not finished
            delay(100)
            print("\ncancelling parent")
            cancel()
        }

        assertThat(hasRunChild1).isTrue()
        assertThat(hasRunChild2).isFalse()
    }

    @Test
    fun `values set after delay() do not persist`() = runBlockingTest {
        var hasRunChild1: String? = null
        launch {

            launch {
                print("\nstarting child1:")
                delay(1)
                hasRunChild1 = "set from child coroutine"
                print("\ncompleted child1")
            }
        }

        // for some weird reason if delay is called prior to a value being set, it won't be set
        assertThat(hasRunChild1).isNull()
    }

    @Test
    fun `if you cancel a child, siblings don't get cancelled`() = runBlockingTest {

        var hasRunChild1 = false
        var hasRunChild2 = false
        launch {

            launch {
                print("\nstarting child1:")
                print("\ncancelling child 1")
                cancel()
                ensureActive()
                hasRunChild1 = true
                print("\ncompleted child1")
            }

            launch {
                print("\nstarting child2:")
                // this one has longer delay, so should not be run
                hasRunChild2 = true
                print("\ncompleted child2")
            }
        }

        assertThat(hasRunChild1).isFalse()
        assertThat(hasRunChild2).isTrue()
    }

    @Test(expected = Exception::class)
    fun `when a child fails with exception, all children get cancelled`() = runBlockingTest {

        var hasRunChild1 = false
        var hasRunChild2 = false
        val job = launch {

            launch {
                print("\nstarting child1:")
                print("\ncanceling with failing exception in child1")
                throw Exception("egis")

                ensureActive()
                print("\ncompleted child1")
                hasRunChild1 = true
            }

            launch {
                print("\nstarting child2:")
                // this one has longer delay, so should not be run
                delay(5000)
                ensureActive()
                hasRunChild2 = true
                print("\ncompleted child2")
            }
        }

        // waiting till child coroutines have finished
        job.join()

        assertThat(hasRunChild1).isFalse()
        assertThat(hasRunChild2).isFalse()
    }

    @Test
    fun `cooperative cancellation is needed to finish execution`() = runBlockingTest {

        var hasExecutedPastEnsureActive: Boolean

        launch {
            print("\nstarting child1:")
            print("\ncancelling child 1")
            cancel()
            // you have to call ensureActive() or yield() to mark a point where cancellation will come into effect
            yield()
//            ensureActive()
            hasExecutedPastEnsureActive = true
            print("\ncompleted child1")

            assertThat(hasExecutedPastEnsureActive).isFalse()
        }
    }

    @Ignore("this would finish if sleep() is replaced with delay()")
    @Test
    fun `flow will not stop executing when its scope is cancelled, unless we or another coroutine cancels it`() = runBlocking {

        val job = launch {
            flow {
                while (true) {
                    println("still running...")
                    sleep(100)
                    emit(Unit)
                }
            }.collect {}
        }

        delay(250)
        job.cancel()
    }
}
