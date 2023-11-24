package com.egecius.coroutinesdemo

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.junit.Test

@ExperimentalCoroutinesApi
class ChannelsTest {

    /** Produces infinite stream */
    private fun CoroutineScope.produceNumbers() = produce {
        var x = 1
        while (true) send(x++) // infinite stream of integers starting from 1
    }

    /** squares every received value*/
    private fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for (x in numbers) send(x * x)
    }

    @Test
    fun `a pipeline is a pattern where one coroutine is producing, possibly infinite, stream of values`() = runBlocking {
        val numbers = produceNumbers() // produces integers from 1 and on
        val squares = square(numbers) // squares integers
        repeat(5) {
            println(squares.receive()) // print first five
        }
        println("Done!") // we are done
        coroutineContext.cancelChildren() // cancel children coroutines
    }

    @Test
    fun `produce a stream of prime number using pipes`() = runBlocking {

        var currentChannel: ReceiveChannel<Int> = numbersFrom(2)
        repeat(10) {
            val prime: Int = currentChannel.receive()
            println(prime)
            currentChannel = filter(currentChannel, prime)
        }
        coroutineContext.cancelChildren() // cancel all children to let main finish
    }

    private fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
        var x = start
        while (true) send(x++) // infinite stream of integers from start
    }

    private fun CoroutineScope.filter(numbersChannel: ReceiveChannel<Int>, prime: Int) = produce<Int> {
        for (x in numbersChannel) if (x % prime != 0) send(x)
    }
    
    @Test
    fun `multiple coroutines may receive from the same channel`() = runBlocking {
        val producer = produceNumbersWithDelay()
        repeat(5) { launchProcessor(it, producer) }
        delay(950)
        producer.cancel() // cancel producer coroutine and thus kill them all
    }

    private fun CoroutineScope.produceNumbersWithDelay() = produce {
        var x = 1 // start from 1
        while (true) {
            send(x++) // produce next
            delay(100) // wait 0.1s
        }
    }

    private fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
        for (msg in channel) {
            println("Processor #$id received $msg")
        }
    }
}