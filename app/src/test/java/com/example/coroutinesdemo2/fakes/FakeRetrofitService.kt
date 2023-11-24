@file:Suppress("MemberVisibilityCanBePrivate")

package com.egecius.coroutinesdemo.fakes

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