package com.hunterlc.hmusic.service

import androidx.lifecycle.MutableLiveData
import com.hunterlc.hmusic.data.SongsInnerData

object PlayQueue {

    /* 随机播放的队列 */
    var currentQueue = MutableLiveData<ArrayList<SongsInnerData>>().also {
        it.value = ArrayList()
    }

    private var normalQueue = MutableLiveData<ArrayList<SongsInnerData>>().also {
        it.value = ArrayList()
    }

    private var queue = ArrayList<SongsInnerData>()

    fun setNormal(list: ArrayList<SongsInnerData>) {
        queue = list
        normalQueue.value = list
        normal()
    }

    /**
     * 随机播放
     */
    fun random() {
        val shuffle = currentQueue.value
        shuffle?.shuffle()
        currentQueue.value = shuffle
    }

    fun normal() {
        currentQueue.value?.clear()
        currentQueue.value?.addAll(queue)
    }

}