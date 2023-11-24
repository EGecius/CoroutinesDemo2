package com.example.coroutinesdemo2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.coroutinesdemo2.R
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setClickListener()
//
//        showHowToControlThreadInFlow()
    }

//    private fun showHowToControlThreadInFlow() {
//       GlobalScope.launch(Main) {
//           val name = Thread.currentThread().name
//           Log.v("Eg:MainActivity:33", "showHowToControlThreadInFlow thread name: $name")
//           demoFlow().collect {
//               val name2 = Thread.currentThread().name
//               Log.d("Eg:MainActivity:35", "showHowToControlThreadInFlow() name2: $name2")
//           }
//       }
//    }
//
//    private fun demoFlow(): Flow<Int> {
//        return flow {
//            val name = Thread.currentThread().name
//            Log.w("Eg:MainActivity:43", "demoFlow thread name: $name")
//            emit(1)
//        }.flowOn(Dispatchers.IO)
//    }
//
//    private fun showHowWithContextChangesThreads() {
//        GlobalScope.launch(Dispatchers.Main) {
//            val name = Thread.currentThread().name
//            Log.i("Eg:MainActivity:28", "onCreate thread name: $name")
//            showHowToUseDispatchers()
//            val name2 = Thread.currentThread().name
//            Log.i("Eg:MainActivity:31", "onCreate thread name: $name2")
//        }
//    }
//
//    private suspend fun showHowToUseDispatchers() {
//        withContext(Dispatchers.IO) {
//            val name = Thread.currentThread().name
//            Log.i("Eg:MainActivity:39", "showHowToUseDispatchers thread name: $name")
//        }
//    }
//
//    private fun demoLaunchWhenStarted() {
//        // Flow will pause emitting on onStop() but will continue emitting on onStart()
//        lifecycleScope.launchWhenStarted {
//            neverEndingEmittingFlow().collect {
//                Log.v("Eg:MainActivity:29", "onCreate() it: $it")
//            }
//        }
//
//        Log.v("Eg:MainActivity:35", "onCreate() emitIn5s")
//
//        // Coroutine will keep running after onStop() but will emit only on onStart()
//        lifecycleScope.launchWhenStarted {
//            val result = emitIn5s()
//            Log.v("Eg:MainActivity:38", "onCreate() result: $result")
//        }
//    }
//
//    private suspend fun emitIn5s(): String {
//        delay(5_000)
//        return "emission after 5s"
//    }
//
//    private fun setClickListener() {
//        button.setOnClickListener {
//            fetchFakeApiRequestAndUpdateUi()
//        }
//    }
//
//    private fun fetchFakeApiRequestAndUpdateUi() {
//        CoroutineScope(Dispatchers.IO).launch {
//            // starting in IO thread
//            val result1 = getResultFromApi(RESULT_1)
//            println("debug: $result1")
//            // now will switch to Main thread to update the UI:
//            setTextOnMainThread(result1)
//            // now will get another result which still will be fetched on IO thread:
//            val result2 = getResultFromApi(RESULT_2)
//            println("debug: $result2")
//            // now will switch to Main thread to update the UI:
//            setTextOnMainThread(result2)
//        }
//    }
//
//    private suspend fun getResultFromApi(result: String): String {
//        logThread("getResult1FromApi")
//        delay(1000)
//        return result
//    }
//
//    private fun logThread(methodName: String) {
//        println("debug: ${methodName}: ${Thread.currentThread().name}")
//    }
//
//    private suspend fun setTextOnMainThread(input: String) {
//        withContext(Dispatchers.Main) {
//            val newText = text_view.text.toString() + "\n" + input
//            text_view.text = newText
//        }
//    }
//
//    companion object {
//        private const val RESULT_1 = "Result 1"
//        private const val RESULT_2 = "Result 2"
//    }
}

fun neverEndingEmittingFlow(): Flow<Unit> {
    return flow {
        while (true) {
            delay(1_000)
            emit(Unit)
        }
    }
}
