package com.egecius.coroutinesdemo

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

@FlowPreview
class FlowFlatteningTest {

    private fun requestFlow(i: Int): Flow<String> = flow {
        emit("$i: First")
        delay(50) // wait 500 ms
        emit("$i: Second")
    }

    @Suppress("UNUSED_VARIABLE")
    @Test
    fun `flatMapConcat allows mapping Flow to another Flow`() = runBlocking {
        // without flattening you get Flow of Flows
        val flow: Flow<Flow<String>> = (1..3).asFlow()
            .map { requestFlow(it) }

        // with flatMapConcat we avoid it
        val flow2: Flow<String> = (1..3).asFlow()
            .flatMapConcat { requestFlow(it) }

        flow2.collect {
            println(it)
        }
    }

    @Test
    fun `flatMapMerge does not wait for inner Flow to complete`() = runBlocking {
        val flow: Flow<String> = (1..3).asFlow()
            .flatMapMerge { requestFlow(it) }

        flow.collect {
            println(it)
        }
    }
}