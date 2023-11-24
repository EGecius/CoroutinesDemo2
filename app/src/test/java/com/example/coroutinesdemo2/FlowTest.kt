package com.egecius.coroutinesdemo

import com.egecius.coroutinesdemo.util.log
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import kotlin.system.measureTimeMillis

@Suppress("BlockingMethodInNonBlockingContext")
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class FlowTest {

    @Test
    fun `toList() operator adds all values to a list - makes testing flow easy`() = runBlockingTest {
        val result = (1..3).asFlow()
            .map { it * it }
            .toList()

        println(result)
        assertThat(result).isEqualTo(listOf(1, 4, 9))
    }

    @Test
    fun `toList() operator collects even when delay() is called between emissions`() = runBlockingTest {
        val flow = flow {
            for (value in 1..3) {
                emit(value)
                delay(10)
            }
        }

        assertThat(flow.toList()).isEqualTo(listOf(1, 2, 3))
    }

    @Test
    fun `show how to create sequence flow`() {

        fun foo(): Sequence<Int> = sequence { // sequence builder
            for (i in 1..3) {
                Thread.sleep(100) // pretend we are computing it
                yield(i) // yield next value
            }
        }

        foo().forEach { value -> println(value) }

    }

    @Test
    fun `show how create a flow`() = runBlockingTest {

        val myFlow = flow { // flow builder
            for (i in 1..3) {
                emit(i) // emit next value
            }
        }

        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..3) {
                println("I'm not blocked $k")
                delay(100)
            }
        }
        // Collect the flow
        myFlow.collect {
            println("result: $it")
        }
    }

    @Test
    fun `flows are cold - execution does not start until collect() is called`() = runBlockingTest {

        val myFlow = flow {
            println("Flow started")
            for (i in 1..3) {
                delay(100)
                emit(i)
            }
        }

        println("Calling collect...")
        myFlow.collect {
            println(it)

        }
        println("Calling collect again...")
        myFlow.collect {
            println(it)
        }
    }

    @Test
    fun `flows get cancelled with the same rules as coroutines`() = runBlockingTest {

        fun foo(): Flow<Int> = flow {
            for (i in 1..3) {
                delay(100)
                println("Emitting $i")
                emit(i)
            }
        }

        withTimeoutOrNull(250) { // Timeout after 250ms
            foo().collect {
                println(it)
            }
        }
        println("Done")
    }

    @Test
    fun `flowOf() is a simpler flow builder`() = runBlockingTest {

        flowOf(1, 2, 3).collect {
            // will print 1, 2, 3
            println(it)
        }
    }

    @Test
    fun `asFlow() is a simple flow builder`() = runBlockingTest {
        // will print 1, 2, 3
        (1..3).asFlow().collect { println(it) }
    }

    @Test
    fun `flows can call intermediate operators`() = runBlockingTest {
        (1..3).asFlow() // a flow of requests
            .map { request -> performRequest(request) }
            .collect { response -> println(response) }
    }

    private suspend fun performRequest(request: Int): String {
        delay(100) // imitate long-running asynchronous work
        return "response $request"
    }

    @Test
    fun `transform() operator allows emitting multiple times & multiple types in a single block`() = runBlockingTest {
        (1..3).asFlow()
            .transform {
                // can emit multiple times and multiple types in a block
                val request: Int = it
                emit(request)
                val result: String = performRequest(request)
                emit(result)
            }
            .collect { value -> println(value) }
    }

    @Test
    fun `take cancels the execution of the flow when the corresponding limit is reached`() = runBlockingTest {
        val numbers: Flow<Int> = flow {
            try {
                emit(1)
                emit(2)
                println("This line will not execute")
                emit(3)
            } catch (e: Exception) {
                println("exception: $e")
            } finally {
                println("Finally in numbers")
            }
        }

        numbers
            .take(2) // take only the first two values
            .collect { value -> println(value) }
    }

    @Test
    fun `reduce operator allows accumulate all collected values`() = runBlockingTest {

        val sum = (1..3).asFlow()
            .map { it * it } // squares of numbers from 1 to 3
            .reduce { accumulator, value -> accumulator + value } // sum them (terminal operator)

        println(sum)
        assertThat(sum).isEqualTo(14) // 1 + 4 + 9 = 14
    }

    @Test
    fun `first() operator takes only the first value`() = runBlockingTest {
        val result = (1..3).asFlow()
            .map { it * it }
            .first()

        println(result)
        assertThat(result).isEqualTo(1)
    }

    @Test
    fun `flows are sequential`() = runBlockingTest {
        // execution of the next value does not start until execution of the one before is complete.
        (1..3).asFlow()
            .filter {
                println("Filter $it")
                it % 2 == 0
            }
            .map {
                println("Map $it")
                "string $it"
            }.collect {
                println("Collect $it")
            }
    }

    @Test
    fun `to have Flow executed on background thread but values emitted on the main thread use 'flowOn()'`() = runBlocking {
        val myFlow = flow {
            for (i in 1..3) {
                Thread.sleep(100) // pretend we are computing it in CPU-consuming way
                log("Emitting $i")
                emit(i) // emit next value
            }
        }.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder

        myFlow.collect { value ->
            log("Collected $value")
        }
    }

    @Test
    fun `buffer operator keeps Flow executing without waiting for collection to finish`() = runBlocking {

        val myFlow = flow {
            for (i in 1..3) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                emit(i)
            }
        }

        val time = measureTimeMillis {
            myFlow
                .buffer()
                .collect { value ->
                    delay(200) // pretend we are processing it for 300 ms
                    println(value)
                }
        }
        // without buffer it takes 900ms = 3*100ms + 3*300ms
        // with buffer it takes 700ms = 1*100ms + 3*200ms
        println("Collected in $time ms")
    }

    @Test
    fun `conflate() collects only most recent values, when collection is slow`() = runBlocking {
        val myFlow = flow {
            for (i in 1..3) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                emit(i)
            }
        }

        val time = measureTimeMillis {
            myFlow
                .conflate() // conflate emissions, don't process each one
                .collect { value ->
                    delay(300) // pretend we are processing it for 300 ms
                    // value 2 will be skipped because collect() is too slow -- value 3 before collect() is finished
                    println(value)
                }
        }
        println("Collected in $time ms")
    }

    @Test
    fun `collectLatest() cancels a slow collector and restarts it every time a new value is emitted`() = runBlocking {

        val myFlow = flow {
            for (i in 1..2) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                emit(i)
            }
        }

        val time = measureTimeMillis {
            myFlow
                .collectLatest { value -> // cancel & restart on the latest value
                    println("Collecting $value")
                    delay(300) // pretend we are processing it for 300 ms
                    println("Done $value")
                }
        }
        println("Collected in $time ms")
    }

    @Test
    fun ` zip operator that combines the corresponding values of two flows`() = runBlocking {
        val numbersFlow = (1..3).asFlow()
        val stringsFlow = flowOf("one", "two", "three")
        numbersFlow.zip(stringsFlow) { a, b -> "$a -> $b" } // compose a single string
            .collect { println(it) }
    }

    @Test
    fun `combine() operator uses latest values, like RxJava's combineLatest`() = runBlocking {

        val numbersFlow = (1..3).asFlow().onEach { delay(30) } // numbers 1..3 every 300 ms
        val stringsFlow = flowOf("one", "two", "three").onEach { delay(40) } // strings every 400 ms
        val startTime = System.currentTimeMillis() // remember the start time
        numbersFlow.combine(stringsFlow) { a, b -> "$a -> $b" } // compose a single string with "combine"
            .collect { value -> // collect and print
                println("$value at ${System.currentTimeMillis() - startTime} ms from start")
            }
    }

}
