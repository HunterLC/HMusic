package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.network.repository.Repository
import com.hunterlc.hmusic.util.LogUtil

class HomeFragmentViewModel : ViewModel() {
    private val _getBannerLiveData = MutableLiveData<Int>().also {
        it.value = 0
    }

    private val _getRecommendPlaylistLiveData = MutableLiveData<Int>()
    private val _getDailyPlaylistLiveData = MutableLiveData<Int>()

    var bannerInfoLiveData = Transformations.switchMap(_getBannerLiveData) {
        Repository.getBanner()
    }

    var recommendPlaylistLiveData = Transformations.switchMap(_getRecommendPlaylistLiveData) {
        Repository.getRecommendPlaylist()
    }

    var dailyPlaylistLiveData = Transformations.switchMap(_getDailyPlaylistLiveData) {
        Repository.getRecommendDaily(MyApplication.userManager.getCloudMusicCookie())
    }

    fun getBanner(){
        _getBannerLiveData.value?.plus(1) //使_getBannerLiveData发生变化
    }

    fun getRecommendPlaylist(){
        _getRecommendPlaylistLiveData.value = 0 //_getRecommendPlaylistLiveData发生变化
    }

    fun getDailyPlaylist(){
        _getDailyPlaylistLiveData.value = 0 //_getDailyPlaylistLiveData发生变化
    }
}