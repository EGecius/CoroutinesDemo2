package com.example.coroutinesdemo2

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class MyViewModel : ViewModel() {

    private val fakeRepo = FakeRepo()

    private val selectedItemId = MutableLiveData("1")

    // you can use switchMap to map to another liveData object
    val result: LiveData<FakeItem> = selectedItemId.switchMap {
        liveData { emit(fetchItem()) }
    }

    private suspend fun fetchItem(): FakeItem {
        return fakeRepo.fetchItem()
    }

    fun startModelling() {
        installThreadHandler()
        demoInvokeOnCompletion()
    }

    private fun installThreadHandler() {
        val handler = Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
            Log.w("Eg:MyViewModel:26", "startModelling() e: $e")
        }
        Thread.setDefaultUncaughtExceptionHandler(handler)
    }

    private fun demoInvokeOnCompletion() {
        val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.v("Eg:MyViewModel:30", "demoInvokeOnCompletion() throwable: $throwable")
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            willFinishIn1s()
            willCancelItself()
            willPrintLog()
        }.invokeOnCompletion {
//            Log.v("Eg:MyViewModel:31", "demoInvokeOnCompletion() it: $it")
        }
    }

    private fun willPrintLog() {
        Log.i("Eg:MyViewModel:51", "willPrintLog() ")
    }

    private suspend fun willCancelItself() {
        delay(1)
        throw CancellationException()
    }

    private suspend fun willFinishIn1s() {
        delay(1_000)
    }

    private suspend fun willFail() {
        delay(1)
        throw EgisException()
    }

    /** This shows how LiveData delegates to another LiveData */
    val resultFromSoruce: LiveData<FakeItem> = selectedItemId.switchMap {
        liveData { emitSource(fakeRepo.getLiveData()) }
    }
}
