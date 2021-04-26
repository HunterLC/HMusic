package com.hunterlc.hmusic.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.SongsInnerData
import com.hunterlc.hmusic.manager.interfaces.MusicControllerInterface
import com.hunterlc.hmusic.room.toSongList
import com.hunterlc.hmusic.util.ConfigUtil
import com.hunterlc.hmusic.util.runOnMainThread
import kotlin.concurrent.thread

/**
 * 音乐服务连接
 */
class MusicServiceConnection : ServiceConnection {

    /**
     * 服务连接后
     */
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        MyApplication.musicController.value = p1 as MusicControllerInterface
        thread {
            // 恢复 SongData
            val recoverSong = MyApplication.mmkv.decodeParcelable(ConfigUtil.SERVICE_CURRENT_SONG, SongsInnerData::class.java)
            val recoverProgress = MyApplication.mmkv.decodeInt(ConfigUtil.SERVICE_RECOVER_PROGRESS, 0)
            val recoverPlayQueue = MyApplication.appDatabase.playQueueDao().loadAll().toSongList()
            recoverSong?.let { song ->
                // recover = true
                if (recoverSong in recoverPlayQueue) {
                    runOnMainThread {
                        MyApplication.musicController.value?.let {
                            it.setRecover(true)
                            it.setRecoverProgress(recoverProgress)
                            it.setPlaylist(recoverPlayQueue)
                            it.playMusic(song)
                        }
                    }
                }
            }
        }
    }

    /**
     * 服务意外断开连接
     */
    override fun onServiceDisconnected(p0: ComponentName?) {
        MyApplication.musicController.value = null
    }

}