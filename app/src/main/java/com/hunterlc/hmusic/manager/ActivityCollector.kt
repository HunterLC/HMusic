package com.hunterlc.hmusic.manager

import android.app.Activity

object ActivityCollector {

    //保存所有的Activity
    private val activities = ArrayList<Activity>()

    /***
     * 添加Activity
     */
    fun addActivity(activity: Activity){
        activities.add(activity)
    }

    /***
     * 移除Activity
     */
    fun removeActivity(activity: Activity){
        activities.remove(activity)
    }

    /***
     * 结束所有的Activities
     */
    fun finishAll(){
        for (activity in activities){
            if (!activity.isFinishing){
                activity.finish()
            }
        }
        activities.clear()
    }
}