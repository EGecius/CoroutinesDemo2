package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import org.junit.Test

@ExperimentalCoroutinesApi
class ChannelTest {

    @Test
    fun `channels acts like a blockingQueue where 'put' is 'send' & 'take' is 'receive'`() = runBlocking {
        val channel = Channel<Int>()
        launch {
            // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
            for (x in 1..5) channel.send(x * x)
        }
        // here we print five received integers:
        repeat(5) { println(channel.receive()) }
        println("Done!")
    }

    @Test
    fun `unlike a queue, a channel can be closed to indicate that no more elements are coming`() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) channel.send(x * x)
            channel.close() // we're done sending
        }
        // here we print received values using `for` loop (until the channel is closed)
        for (y in channel) println(y)
        println("Done!")
    }


    @Test
    fun `produce builder makes it easy to produce a channel, for loop can be replaced with consumeEach`() = runBlocking {

        val squaresChannel = produce {
            for (x in 1..5) {
                send(x * x)
            }
        }
        squaresChannel.consumeEach { println(it) }
        println("Done!")
    }

    @Test
    fun `multiple coroutines may send to the same channel`() = runBlocking {
        val channel = Channel<String>()
        launch { sendString(channel, "foo", 200L) }
        launch { sendString(channel, "BAR!", 500L) }
        repeat(6) { // receive first six
            println(channel.receive())
        }
        coroutineContext.cancelChildren() // cancel all children to let main finish

    }

    private suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
        while (true) {
            delay(time)
            channel.send(s)
        }
    }
}