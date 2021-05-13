package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.network.repository.Repository

class DailySongsViewModel: ViewModel() {
    private val cookieChangeLiveData = MutableLiveData<String>().also {
        it.value = ""
    }

    var cookieLiveData = Transformations.switchMap(cookieChangeLiveData) { cookie ->
        Repository.getDailySongs(cookie)
    }

    fun getDailySongs(cookie: String){
        cookieChangeLiveData.value = cookie
    }
}