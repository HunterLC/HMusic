package com.hunterlc.hmusic.ui.viewmodel

import android.graphics.Color
import androidx.lifecycle.*
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.LyricViewData
import com.hunterlc.hmusic.manager.VolumeManager
import com.hunterlc.hmusic.network.repository.Repository
import com.hunterlc.hmusic.util.ConfigUtil

class PlayerViewModel : ViewModel() {
    companion object {
        val DEFAULT_COLOR = Color.rgb(90, 90, 90)
    }

    var rotation = 0f
    var rotationBackground = 0f

    // 播放模式
    var playMode = MutableLiveData<Int>().also {
        it.value = MyApplication.musicController.value?.getPlayMode()
    }

    var duration = MutableLiveData<Int>().also {
        it.value = MyApplication.musicController.value?.getDuration()
    }

    var progress = MutableLiveData<Int>().also {
        it.value = MyApplication.musicController.value?.getProgress()
    }

    var lyricTranslation = MutableLiveData<Boolean>().also {
        it.value = MyApplication.mmkv.decodeBool(ConfigUtil.LYRIC_TRANSLATION, true)
    }

    // 对内
    private var _lyricViewData = MutableLiveData<LyricViewData>().also {
        it.value = LyricViewData("", "")
    }

    var lyricViewData = MutableLiveData<LyricViewData>().also {
        it.value = LyricViewData("", "")
    }

    var currentVolume = MutableLiveData<Int>().also {
        it.value = VolumeManager.getCurrentVolume()
    }

    // 界面按钮颜色
    var color = MutableLiveData<Int>().also {
        it.value = Color.rgb(100, 100, 100)
    }

    //CD封面设置方形
    var square_cd = MutableLiveData<Boolean>().also{
        it.value = MyApplication.mmkv.decodeBool(ConfigUtil.SQUARE_CD_COVER,false)
    }

    //动态流光背景
    var dynamic_background = MutableLiveData<Boolean>().also{
        it.value = MyApplication.mmkv.decodeBool(ConfigUtil.DYNAMIC_BACKGROUND,false)
    }

    var currentLyric = MutableLiveData<String?>()

    /**
     * 刷新
     */
    fun refresh() {
        playMode.value = MyApplication.musicController.value?.getPlayMode()
        if (duration.value != MyApplication.musicController.value?.getDuration()) {
            duration.value = MyApplication.musicController.value?.getDuration()
        }
    }

    /**
     * 刷新 progress
     */
    fun refreshProgress() {
        progress.value = MyApplication.musicController.value?.getProgress()
    }

    /**
     * 改变播放状态
     */
    fun changePlayState() {
        val nowPlayState = MyApplication.musicController.value?.isPlaying()?.value?: false
        if (nowPlayState) {
            MyApplication.musicController.value?.pause()
        } else {
            MyApplication.musicController.value?.play()
        }
    }

    /**
     * 播放上一曲
     */
    fun playLast() {
        MyApplication.musicController.value?.playPrevious()
    }

    /**
     * 播放下一曲
     */
    fun playNext() {
        MyApplication.musicController.value?.playNext()
    }

    /**
     * 改变播放模式
     */
    fun changePlayMode() {
        MyApplication.musicController.value?.changePlayMode()
    }

    /**
     * 设置 progress
     */
    fun setProgress(newProgress: Int) {
        MyApplication.musicController.value?.setProgress(newProgress)
    }

    /**
     * 喜欢音乐
     * true
     */
    fun likeMusic(success: (Boolean) -> Unit) {

    }

    var searchLyric = MutableLiveData<Long>()

    var lyricLiveData = Transformations.switchMap(searchLyric) { id ->
        Repository.getLyricById(id)
    }

    /**
     * 更新歌词
     */
    fun getLyricById(id: Long) {
        searchLyric.value = id
    }

    /**
     * 设置歌词翻译
     */
    var noLyric = MutableLiveData<LyricViewData>().also {
        it.value = LyricViewData("", "")
    }
    var hasLyric = MutableLiveData<LyricViewData>().also {
        it.value = LyricViewData("", "")
    }

    fun setLyricTranslation(open: Boolean) {
        lyricTranslation.value = open
        if (lyricTranslation.value == true) {
            lyricViewData.value = hasLyric.value
        } else {
            lyricViewData.value = noLyric.value
        }
        // updateLyric()
        MyApplication.mmkv.encode(ConfigUtil.LYRIC_TRANSLATION, open)
    }

    /**
     * 音量加
     */
    fun addVolume() {
        currentVolume.value?.let {
            if (it < VolumeManager.maxVolume) {
                currentVolume.value = currentVolume.value!!.plus(5)
            } else {
                currentVolume.value = VolumeManager.maxVolume
            }
            VolumeManager.setStreamVolume(currentVolume.value!!)
        }
    }

    /**
     * 音量减
     */
    fun reduceVolume() {
        currentVolume.value?.let {
            if (it > 0) {
                currentVolume.value = currentVolume.value!!.minus(5)
            } else {
                currentVolume.value = 0
            }
            VolumeManager.setStreamVolume(currentVolume.value!!)
        }
    }

}