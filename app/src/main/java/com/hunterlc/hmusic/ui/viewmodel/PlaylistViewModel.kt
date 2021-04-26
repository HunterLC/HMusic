package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.*
import com.hunterlc.hmusic.network.repository.Repository

class PlaylistViewModel : ViewModel() {
    // id
    var id = MutableLiveData<Long>().also {
        it.value = 0L
    }

    //歌单里歌曲id
    var playlistMusicIdLiveData = Transformations.switchMap(id) { id ->
        Repository.getMusicsByPlaylistId(id)
    }



    var idsLiveData = MutableLiveData<String>().also {
        it.value = ""
    }

    // 歌曲列表
    var playlist = Transformations.switchMap(idsLiveData) { ids ->
        Repository.getMusicDetail(ids)
    }

    fun getMusicDetail(ids: String) {
        idsLiveData.value = ids
    }

    fun getMusicsByPlaylistId(id: Long) {
        this.id.value = id
    }

}