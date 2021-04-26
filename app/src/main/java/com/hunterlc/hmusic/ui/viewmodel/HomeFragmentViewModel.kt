package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.network.repository.Repository

class HomeFragmentViewModel : ViewModel() {
    private val _getBannerLiveData = MutableLiveData<Int>().also {
        it.value = 0
    }

    var bannerInfoLiveData = Transformations.switchMap(_getBannerLiveData) {
        Repository.getBanner()
    }

    fun getBanner(){
        _getBannerLiveData.value = _getBannerLiveData.value?.plus(1) //使_getBannerLiveData发生变化
    }
}