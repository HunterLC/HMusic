package com.hunterlc.hmusic.manager

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.hunterlc.hmusic.manager.interfaces.ActivityManagerInterface
import com.hunterlc.hmusic.ui.activity.*
import com.hunterlc.hmusic.util.LogUtil

class ActivityManager: ActivityManagerInterface {

    override fun startMainActivity(activity: Activity) {
        LogUtil.i("ActivityManager","startMainActivity")
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startPlayerActivity(activity: Activity) {
        LogUtil.i("ActivityManager","startPlayerActivity")
        val intent = Intent(activity, PlayerActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startSettingActivity(activity: Activity) {
        LogUtil.i("ActivityManager","startSettingActivity")
        val intent = Intent(activity, SettingActivity::class.java)
        activity.startActivity(intent)
    }

    override fun startLoginActivity(activity: Activity) {
        LogUtil.i("ActivityManager","startLoginActivity")
        val intent = Intent(activity, LoginActivity::class.java)
        activity.startActivity(intent)
    }
    override fun startSplashActivity(activity: Activity) {
        LogUtil.i("ActivityManager","startSplashActivity")
        val intent = Intent(activity,SplashActivity::class.java)
        activity.startActivity(intent)
    }
}