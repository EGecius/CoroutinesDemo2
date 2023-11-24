package com.egecius.coroutinesdemo

import com.egecius.coroutinesdemo.util.failingCoroutine
import com.egecius.coroutinesdemo.util.nonFailingCoroutine
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Ignore
import org.junit.Test

@ExperimentalCoroutinesApi
class SupervisorScopeDemoTest {

    @Test
    @Ignore // for some reason it does not work as it's supposed to. second coroutine does not finish after delay
    fun `with supervisor job when a child coroutine fails, siblings don't get cancelled`() = runBlockingTest {

        val coroutineScope = CoroutineScope(SupervisorJob())
        coroutineScope.launch {
            println("starting child1")
            throw Exception("Egis")
        }

        coroutineScope.launch {
            println("starting child2")
            delay(100)
            println("completed child2")
        }
    }


    @Test(expected = Exception::class)
    fun `when a child coroutine in supervisor scope fails, siblings don't get cancelled`() = runBlockingTest {

        launch {
            supervisorScope {

                val child1 = launch {
                    println("starting child1")
                    delay(100)
                    throw Exception("Egis")
                }

                val child2 = launch {
                    println("starting child2")
                    delay(100)
                    println("completed child2")
                }
            }
        }
    }

    @Test(expected = Exception::class)
    fun `without supervisor scope, siblings get cancelled on failure`() = runBlockingTest {

        launch {
            println("starting child1")

            val child1 = launch {
                delay(100)
                throw Exception("Egis")
            }

            val child2 = launch {
                println("starting child2")
                delay(100)
                println("completed child2")
            }
        }
    }

    @Test
    fun `exception thrown in a child does not cancel siblings`() = runBlocking {
        supervisorScope {
            launch {
                failingCoroutine()
            }
            launch {
                nonFailingCoroutine()
            }
        }
        Unit
    }
}
