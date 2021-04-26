package com.hunterlc.hmusic

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.hunterlc.hmusic.manager.ActivityManager
import com.hunterlc.hmusic.manager.UserManager
import com.hunterlc.hmusic.manager.interfaces.MusicControllerInterface
import com.hunterlc.hmusic.room.AppDatabase
import com.hunterlc.hmusic.service.MusicService
import com.hunterlc.hmusic.service.MusicServiceConnection
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.DarkThemeUtil
import com.tencent.mmkv.MMKV

/***
 * 程序启动时会自动将这个类进行初始化，以便于管理程序内一些全局的状态信息
 */
class MyApplication : Application(){
    companion object{
        @SuppressLint("StaticFieldLeak") //忽略内存泄露警告，因为这里实际上不会发生此问题
        lateinit var context: Context
        lateinit var activityManager: ActivityManager
        lateinit var userManager: UserManager
        lateinit var mmkv: MMKV // mmkv
        var musicController = MutableLiveData<MusicControllerInterface?>().also {
            it.value = null
        }
        val musicServiceConnection by lazy { MusicServiceConnection() } // 音乐服务连接
        // 数据库
        lateinit var appDatabase: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        //全局获取Context对象机制，使用MyApplication.context即可
        context = applicationContext

        // 管理初始化
        activityManager = ActivityManager()
        userManager = UserManager()
        // MMKV 初始化
        MMKV.initialize(this)
        mmkv = MMKV.defaultMMKV() // MMKV
        // 初始化数据库
        appDatabase = AppDatabase.getDatabase(this)
        // 安全检查
        checkSecure()
        if (mmkv.decodeBool(ConfigUtil.DARK_THEME, false)) {
            DarkThemeUtil.setDarkTheme(true)
        }
    }

    /**
     * 安全检查
     */
    private fun checkSecure() {
        // 开启音乐服务
        startMusicService()
    }

    /**
     * 启动音乐服务
     */
    private fun startMusicService() {
        // 通过 Service 播放音乐，混合启动
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        // 绑定服务
        bindService(intent, musicServiceConnection, BIND_AUTO_CREATE)
    }
}