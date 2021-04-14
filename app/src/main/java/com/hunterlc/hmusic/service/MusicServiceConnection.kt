package com.hunterlc.hmusic.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.manager.interfaces.MusicControllerInterface

/**
 * 音乐服务连接
 */
class MusicServiceConnection : ServiceConnection {

    /**
     * 服务连接后
     */
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        MyApplication.musicController.value = p1 as MusicControllerInterface
    }

    /**
     * 服务意外断开连接
     */
    override fun onServiceDisconnected(p0: ComponentName?) {
        MyApplication.musicController.value = null
    }

}