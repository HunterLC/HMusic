package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.network.repository.Repository
import com.hunterlc.hmusic.util.LogUtil

class TopListViewModel: ViewModel() {
    private val topListChangeLiveData = MutableLiveData<Int>().apply {
        value = 0
    }

    var topListLiveData = Transformations.switchMap(topListChangeLiveData) {
        LogUtil.e("调用了吗","调用了吗")
        Repository.getToplist()
    }

    //获取榜单
    fun getTopList(){
        topListChangeLiveData.value?.plus(1)
    }
}