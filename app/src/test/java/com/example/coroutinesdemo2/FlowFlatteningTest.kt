package com.example.coroutinesdemo2

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
class FlowFlatteningTest {

    private fun abFlowWithDelay(i: Int): Flow<String> = flow {
        emit("$i: A")
        delay(50) // wait 500 ms
        emit("$i: B")
    }

    @Suppress("UNUSED_VARIABLE")
    @Test
    fun `flatMapConcat allows mapping Flow to another Flow`() = runBlocking {
        // without flattening you get Flow of Flows
        println("Flow 1:")
        (1..3).asFlow()
            .map { abFlowWithDelay(it) }
            .collect {
                println("inner flow:")
                it.collect {
                    println(it)
                }
            }

        println("\nFlow 2:")
        // with flatMapConcat we avoid it
        val flow2: Flow<String> = (1..2).asFlow()
            .flatMapConcat { abFlowWithDelay(it) }

        flow2.toList() shouldBe listOf("1: A", "1: B", "2: A", "2: B")
    }

    @Test
    fun `flatMapMerge does not wait for inner Flow to complete`() = runBlocking {
        val flow: Flow<String> = (1..2).asFlow()
            .flatMapMerge { abFlowWithDelay(it) }

        flow.toList() shouldBe listOf("1: A", "2: A", "1: B", "2: B")
    }
}
