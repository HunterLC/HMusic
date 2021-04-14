package com.hunterlc.hmusic.manager.interfaces

import android.app.Activity

interface ActivityManagerInterface {

    fun startMainActivity(activity: Activity)
    fun startPlayerActivity(activity: Activity)
    fun startSettingActivity(activity: Activity)
    fun startLoginActivity(activity: Activity)
    fun startSplashActivity(activity: Activity)
}