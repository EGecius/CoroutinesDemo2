package com.egecius.coroutinesdemo.fakes

import com.egecius.coroutinesdemo.fakes.FakeCall.Companion.FAKE_RESULT
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/** Mimics Retrofit's Call class */
class FakeCall {

    fun cancel() {
        print("FakeCall.cancel()")
    }

    fun enqueue(callBack: CallBack) {
        print("FakeCall.enqueue()")
        // pretending that the API will respond immediately
        callBack.onResponse()
    }

    interface CallBack {
        fun onResponse()
        fun onFailure(exception: Exception)
    }

    companion object {

    	const val FAKE_RESULT = "Egis result"

    }
}

/** An example how to convert Retrofit's standard APIs to Coroutines */
@ExperimentalCoroutinesApi
suspend fun FakeCall.await() : String {
    return suspendCancellableCoroutine { continuation : CancellableContinuation<String> ->
        continuation.invokeOnCancellation {
            cancel()
        }

        enqueue(object : FakeCall.CallBack {
            override fun onResponse() {
                print("FakeCall.onResponse")
                continuation.resume(FAKE_RESULT) {
                    print(it)
                }
            }

            override fun onFailure(exception: Exception) {
                print("FakeCall.onFailure")
                continuation.resumeWithException(exception)
            }
        })
    }
}