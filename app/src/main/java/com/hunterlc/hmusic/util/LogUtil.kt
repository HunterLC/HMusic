package com.hunterlc.hmusic.util

import android.util.Log

/***
 * 日志类，开发阶段可使用LOG_LEVEL_VERBOSE，发布阶段可使用LOG_LEVEL_ERROR
 */
object LogUtil {
    //最为琐碎的日志，不重要，重要等级为最低
    private const val LOG_LEVEL_VERBOSE = 1

    //打印一些调试信息，这些信息对调试程序和分析问题应该是有帮助
    private const val LOG_LEVEL_DEBUG = 2

    //打印一些比较重要的数据，这些数据应该是你非常想看到的，可以帮你分析用户行为的那种
    private const val LOG_LEVEL_INFO = 3

    //打印一些警告信息，提示程序在这个地方可能会有潜在的风险，最好去修复一下这些出现警告的地方
    private const val LOG_LEVEL_WARN = 4

    //用于打印程序中的错误信息，比如程序进入到了catch 语句当中。当有错误信息打印出来的时候，一般都代表你的程序出现严重问题了，必须尽快修复
    private const val LOG_LEVEL_ERROR = 5

    //自定义日志打印的等级
    private var level = LOG_LEVEL_VERBOSE

    fun v(tag: String, msg: String){
        if (level <= LOG_LEVEL_VERBOSE){
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String){
        if (level <= LOG_LEVEL_DEBUG){
            Log.d(tag, msg)
        }
    }

    fun i(tag: String, msg: String){
        if (level <= LOG_LEVEL_INFO){
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String){
        if (level <= LOG_LEVEL_WARN){
            Log.w(tag, msg)
        }
    }

    fun e(tag: String, msg: String){
        if (level <= LOG_LEVEL_ERROR){
            Log.e(tag, msg)
        }
    }
}