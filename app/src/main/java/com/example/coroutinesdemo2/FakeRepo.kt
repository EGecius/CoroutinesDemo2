package com.egecius.coroutinesdemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay

class FakeRepo {

    suspend fun fetchItem(): FakeItem {
        delay(100)
        return FakeItem()
    }

    fun getLiveData(): LiveData<FakeItem> {
        return MutableLiveData(FakeItem())
    }
}
