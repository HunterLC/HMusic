package com.hunterlc.hmusic.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import androidx.core.content.ContextCompat.startActivity
import com.hunterlc.hmusic.BuildConfig
import com.hunterlc.hmusic.MyApplication
import com.hunterlc.hmusic.data.SongsInnerData.ArtistsData
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Kotlin 顶层函数
 */

/**
 * 设置状态栏图标颜色
 * @param dark true 为黑色，false 为白色
 */
fun setStatusBarIconColor(activity: Activity, dark: Boolean) {
    StatusbarUtil.setStatusBarDarkIcon(activity, dark)
}


/**
 * 运行在主线程，更新 UI
 */
fun runOnMainThread(runnable: () -> Unit) {
    Handler(Looper.getMainLooper()).post(runnable)
}


/**
 * dp 转 px
 */
fun dp2px(dp: Float): Int = (dp * MyApplication.context.resources.displayMetrics.density + 0.5f).toInt()

/**
 * 获取系统当前时间
 */
fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

/**
 * 标准歌手数组转文本
 * @param artistList 歌手数组
 * @return 文本
 */
fun parseArtist(artistList: ArrayList<ArtistsData>): String {
    var artist = ""
    for (artistName in 0..artistList.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += artistList[artistName].name
    }
    return artist
}

/**
 * 通过浏览器打开网页
 * @param context
 * @url 网址
 */
fun openUrlByBrowser(context: Context, url: String) {
    if (url != "") {
        try {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse(url)
            intent.data = contentUrl
            startActivity(context, intent, Bundle())
        } catch (e: Exception) {
            //toast("启动外部浏览器失败，请点击更新详情链接手动更新~")
        }
    }
}

/**
 * 毫秒转日期
 */
//fun msTimeToFormatDate(msTime: Long): String {
//    return TimeUtil.msTimeToFormatDate(msTime)
//}

/**
 * 获取状态栏高度
 * @return px 值
 */
fun getStatusBarHeight(window: Window, context: Context): Int {
    return StatusbarUtil.getStatusBarHeight(window, context)
}

/**
 * 获取底部导航栏高度
 */
fun getNavigationBarHeight(activity: Activity): Int {
    return ScreenUtil.getNavigationBarHeight(activity)
}

/**
 * 获取版本号
 */
fun getVisionCode(): Int {
    return BuildConfig.VERSION_CODE
}

/**
 * 获取版本名
 */
fun getVisionName(): String {
    return BuildConfig.VERSION_NAME
}

fun copyToClipboard(activity: Activity, text: String) {
    val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Label", text)
    clipboardManager.setPrimaryClip(clipData)
//  Toast 提示
//  Toast.makeText(,"已复制邮箱地址到剪贴板",Toast.LENGTH_SHORT).show()
}

fun defaultTypeface(context: Context): Typeface {
    return Typeface.createFromAsset(context.assets, "fonts/Moriafly-Regular.ttf")
}

/****************

 * @param key 由官网生成的key
 * @return 返回true表示呼起手Q成功，返回false表示呼起失败
 */
fun joinQQGroup(context: Context, key: String): Boolean {
    val intent = Intent()
    intent.data =
        Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
    // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return try {
        context.startActivity(intent)
        true
    } catch (e: Exception) {
        // 未安装手Q或安装的版本不支持
        false
    }
}

/***
 * MD5加密
 */
fun encodeMD5(text: String): String {
    try {
        //获取md5加密对象
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        //对字符串加密，返回字节数组
        val digest:ByteArray = instance.digest(text.toByteArray())
        var sb : StringBuffer = StringBuffer()
        for (b in digest) {
            //获取低八位有效值
            var i :Int = b.toInt() and 0xff
            //将整数转化为16进制
            var hexString = Integer.toHexString(i)
            if (hexString.length < 2) {
                //如果是一位的话，补0
                hexString = "0" + hexString
            }
            sb.append(hexString)
        }
        return sb.toString()

    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}