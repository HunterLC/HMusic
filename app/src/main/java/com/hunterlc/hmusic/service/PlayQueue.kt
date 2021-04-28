package com.hunterlc.hmusic.service

import androidx.lifecycle.MutableLiveData
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.room.PlayQueueData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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
        savePlayQueue()
    }

    fun normal() {
        currentQueue.value?.clear()
        currentQueue.value?.addAll(queue)
        savePlayQueue()
    }

    /**
     * 保存歌单到数据库
     */
    private fun savePlayQueue() {
        GlobalScope.launch {
            MyApplication.appDatabase.playQueueDao().loadAll().forEach {
                MyApplication.appDatabase.playQueueDao().deleteById(it.songData.id)
            }
            currentQueue.value?.let {
                for (song in it) {
                    MyApplication.appDatabase.playQueueDao().insert(PlayQueueData(song))
                }
            }
        }
    }

}