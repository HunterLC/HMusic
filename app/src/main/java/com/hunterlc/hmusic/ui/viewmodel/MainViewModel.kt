package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.repository.Repository
import com.hunterlc.hmusic.util.ConfigUtil

class MainViewModel: ViewModel() {

    var singleColumnPlaylist = MutableLiveData<Boolean>().also {
        it.value = false
    }

    // 状态栏高度
    val statusBarHeight = MutableLiveData<Int>().also {
        it.value = 0
    }

    val userId =  MutableLiveData<Long>().also {
        it.value = MyApplication.userManager.getCurrentUid()
    }

    // dp to px
    val scale = MutableLiveData<Float>().also {
        it.value = MyApplication.context.getResources().getDisplayMetrics().density
    }

    var userInfoLiveData = Transformations.switchMap(userId) { uid ->
        Repository.getUserDetail(uid)
    }

    /**
     * 设置用户 id
     */
    fun setUserId() {
        userId.value = MyApplication.userManager.getCurrentUid()
    }

    fun updateUI() {
        singleColumnPlaylist.value = MyApplication.mmkv.decodeBool(ConfigUtil.SINGLE_COLUMN_USER_PLAYLIST, false)
    }

}