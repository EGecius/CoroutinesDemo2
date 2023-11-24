@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.coroutinesdemo2.fakes

import kotlinx.coroutines.ExperimentalCoroutinesApi

class FakeRetrofitService {

    fun getData(): FakeCall {
        return FakeCall()
    }

    @ExperimentalCoroutinesApi
    suspend fun getDataAsCoroutine(): String {
        return getData().await()
    }
}
