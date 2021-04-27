package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.network.repository.Repository

class SearchViewModel: ViewModel() {
    private val searchDefaultChangeLiveData = MutableLiveData<Int>().apply {
        value = 0
    }

    var searchDefaultLiveData = Transformations.switchMap(searchDefaultChangeLiveData) {
        Repository.getSearchDefault()
    }

    fun getSearchDefault(){
        searchDefaultChangeLiveData.value?.plus(1)  //改变值，自动调用请求默认关键字
    }
}