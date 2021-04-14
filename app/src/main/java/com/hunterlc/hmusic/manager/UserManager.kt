package com.hunterlc.hmusic.manager

import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.manager.interfaces.UserManagerInterface
import com.hunterlc.hmusic.util.ConfigUtil

/**
 * user 管理，面向本地
 */
class UserManager: UserManagerInterface {

    companion object {
        const val defaultUid = 0L // 默认 0L，可设置一个默认用户
        const val DEFAULT_COOKIE = ""
    }

    override fun isUidLogin(): Boolean {
        val uid = MyApplication.mmkv.decodeLong(ConfigUtil.UID, defaultUid)
        return uid != defaultUid
    }

    override fun getCurrentUid(): Long {
        return MyApplication.mmkv.decodeLong(ConfigUtil.UID, defaultUid)
    }

    override fun setUid(uid: Long) {
        MyApplication.mmkv.encode(ConfigUtil.UID, uid)
    }

    override fun getCloudMusicCookie(): String {
        return MyApplication.mmkv.decodeString(ConfigUtil.CLOUD_MUSIC_COOKIE, DEFAULT_COOKIE)
    }

    /**
     * 设置网易云音乐用户 Cookie
     */
    override fun setCloudMusicCookie(cookie: String) {
        MyApplication.mmkv.encode(ConfigUtil.CLOUD_MUSIC_COOKIE, cookie)
    }

    override fun hasCookie(): Boolean {
        return MyApplication.userManager.getCloudMusicCookie().isNotEmpty()
    }

}