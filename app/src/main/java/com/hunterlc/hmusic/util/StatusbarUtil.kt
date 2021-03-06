package com.hunterlc.hmusic.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object StatusbarUtil {
    private var mSetStatusBarColorIcon: Method? = null
    private var mSetStatusBarDarkIcon: Method? = null
    private var mStatusBarColorFiled: Field? = null
    private var SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 0

    @SuppressLint("PrivateApi")
    fun getStatusBarHeight(window: Window, context: Context): Int {
        val localRect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(localRect)
        var statusBarHeight = localRect.top
        if (0 == statusBarHeight) {
            try {
                val localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 =
                    localClass.getField("status_bar_height")[localObject].toString().toInt()
                statusBarHeight = context.resources.getDimensionPixelSize(i5)
            } catch (var6: ClassNotFoundException) {
                var6.printStackTrace()
            } catch (var7: IllegalAccessException) {
                var7.printStackTrace()
            } catch (var8: InstantiationException) {
                var8.printStackTrace()
            } catch (var9: NumberFormatException) {
                var9.printStackTrace()
            } catch (var10: IllegalArgumentException) {
                var10.printStackTrace()
            } catch (var11: SecurityException) {
                var11.printStackTrace()
            } catch (var12: NoSuchFieldException) {
                var12.printStackTrace()
            }
        }
        if (0 == statusBarHeight) {
            val resourceId: Int =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return statusBarHeight
    }

    /**
     * ???????????????????????????
     *
     * @param color ??????
     * @param level ??????
     * @return
     */
    fun isBlackColor(color: Int, level: Int): Boolean {
        val grey = toGrey(color)
        return grey < level
    }

    /**
     * ????????????????????????
     *
     * @param rgb ??????
     * @return????????????
     */
    fun toGrey(rgb: Int): Int {
        val blue = rgb and 0x000000FF
        val green = rgb and 0x0000FF00 shr 8
        val red = rgb and 0x00FF0000 shr 16
        return red * 38 + green * 75 + blue * 15 shr 7
    }

    /**
     * ?????????????????????????????????
     *
     * @param activity ??????activity
     * @param color    ??????
     */
    fun setStatusBarDarkIcon(activity: Activity, color: Int) {
        if (mSetStatusBarColorIcon != null) {
            try {
                mSetStatusBarColorIcon!!.invoke(activity, color)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        } else {
            val whiteColor = isBlackColor(color, 50)
            if (mStatusBarColorFiled != null) {
                setStatusBarDarkIcon(activity, whiteColor, whiteColor)
                setStatusBarDarkIcon(activity.window, color)
            } else {
                setStatusBarDarkIcon(activity, whiteColor)
            }
        }
    }

    /**
     * ?????????????????????????????????(???????????????activity??????)
     *
     * @param window ????????????
     * @param color  ??????
     */
    fun setStatusBarDarkIcon(window: Window, color: Int) {
        try {
            setStatusBarColor(window, color)
            if (Build.VERSION.SDK_INT > 22) {
                setStatusBarDarkIcon(window.decorView, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param activity ??????activity
     * @param dark     ???????????? true????????? false ?????????
     */
    fun setStatusBarDarkIcon(activity: Activity, dark: Boolean) {
        setStatusBarDarkIcon(activity, dark, true)
    }

    private fun changeMeizuFlag(winParams: WindowManager.LayoutParams, flagName: String, on: Boolean): Boolean {
        try {
            val f = winParams.javaClass.getDeclaredField(flagName)
            f.isAccessible = true
            val bits = f.getInt(winParams)
            val f2 = winParams.javaClass.getDeclaredField("meizuFlags")
            f2.isAccessible = true
            var meizuFlags = f2.getInt(winParams)
            val oldFlags = meizuFlags
            meizuFlags = if (on) {
                meizuFlags or bits
            } else {
                meizuFlags and bits.inv()
            }
            if (oldFlags != meizuFlags) {
                f2.setInt(winParams, meizuFlags)
                return true
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * ?????????????????????
     *
     * @param view
     * @param dark
     */
    private fun setStatusBarDarkIcon(view: View, dark: Boolean) {
        val oldVis = view.systemUiVisibility
        var newVis = oldVis
        newVis = if (dark) {
            newVis or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            newVis and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        if (newVis != oldVis) {
            view.systemUiVisibility = newVis
        }
    }

    /**
     * ?????????????????????
     *
     * @param window
     * @param color
     */
    private fun setStatusBarColor(window: Window, color: Int) {
        val winParams = window.attributes
        if (mStatusBarColorFiled != null) {
            try {
                val oldColor = mStatusBarColorFiled!!.getInt(winParams)
                if (oldColor != color) {
                    mStatusBarColorFiled!![winParams] = color
                    window.attributes = winParams
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * ?????????????????????????????????(???????????????activity??????)
     *
     * @param window ????????????
     * @param dark   ???????????? true????????? false ?????????
     */
    fun setStatusBarDarkIcon(window: Window, dark: Boolean) {
        if (Build.VERSION.SDK_INT < 23) {
            changeMeizuFlag(window.attributes, "MEIZU_FLAG_DARK_STATUS_BAR_ICON", dark)
        } else {
            val decorView = window.decorView
            if (decorView != null) {
                setStatusBarDarkIcon(decorView, dark)
                setStatusBarColor(window, 0)
            }
        }
    }

    private fun setStatusBarDarkIcon(activity: Activity, dark: Boolean, flag: Boolean) {
        if (mSetStatusBarDarkIcon != null) {
            try {
                mSetStatusBarDarkIcon!!.invoke(activity, dark)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        } else {
            if (flag) {
                setStatusBarDarkIcon(activity.window, dark)
            }
        }
    }

    init {
        try {
            mSetStatusBarColorIcon = Activity::class.java.getMethod("setStatusBarDarkIcon", Int::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        try {
            mSetStatusBarDarkIcon = Activity::class.java.getMethod("setStatusBarDarkIcon", Boolean::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        try {
            mStatusBarColorFiled = WindowManager.LayoutParams::class.java.getField("statusBarColor")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        try {
            val field = View::class.java.getField("SYSTEM_UI_FLAG_LIGHT_STATUS_BAR")
            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = field.getInt(null)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}