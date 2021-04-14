package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.repository.Repository
import com.hunterlc.hmusic.util.*

class MyFragmentViewModel : ViewModel() {



    private val userIdLiveData = MutableLiveData<Long>()

    var userPlaylistLiveData = Transformations.switchMap(userIdLiveData) { uid ->
        Repository.getUserPlayList(uid)
    }

    var userInfoLiveData = Transformations.switchMap(userIdLiveData) { uid ->
        Repository.getUserDetail(uid)
    }

    /**
     * 更新歌单
     */
    fun getUserPlayList(uid: Long){
        userIdLiveData.value = uid
    }


    /**
     * 清空 Playlist
     */


}