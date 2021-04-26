package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.network.repository.Repository

class VideoViewModel: ViewModel() {
    var id = MutableLiveData<Long>().also {
        it.value = 0L
    }

    var videoPath = Transformations.switchMap(id) { id ->
        Repository.getMvUrl(id)
    }

    fun getMvUrl(id: Long) {
        this.id.value = id
    }
}