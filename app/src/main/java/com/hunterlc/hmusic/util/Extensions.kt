package com.hunterlc.hmusic.util

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.hunterlc.hmusic.data.SongsInnerData.ArtistsData

/**
 * 拓展函数
 */

/**
 * 字节数组转 16 进制字符串
 */
fun ByteArray.toHex(): String? {
    val stringBuilder = StringBuilder("")
    if (this.isEmpty()) {
        return null
    }
    for (element in this) {
        val v = element.toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}

/**
 * 隐藏
 */
fun ViewPager2.hideScrollMode() {
    ViewPager2Util.changeToNeverMode(this)
}

/**
 * dp
 */
fun Int.dp(): Int {
    return dp2px(this.toFloat())
}

/**
 * 判断是否是中文字符
 */
fun Char.isChinese(): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(this)
    if (unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
        || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) // 中日韩象形文字
    {
        return true
    }
    return false
}

fun Long.parseSize(): String {
    val source = this.toDouble()
    if (this < 1_000) {
        return "${String.format("%.2f", source)} KB"
    }
    if (this < 1_000_000) {
        return "${String.format("%.2f", source / 1_000)} KB"
    }
    if (this < 1_000_000_000) {
        return "${String.format("%.2f", source / 1_000_000)} MB"
    }
    return "${String.format("%.2f", source / 1_000_000_000)} GB"
}

/**
 * 混合颜色
 * [color] 是要混合的颜色
 */
fun Int.colorMix(color: Int): Int {
    val red = (Color.red(this) + Color.red(color)) / 2
    val green = (Color.green(this) + Color.green(color)) / 2
    val blue = (Color.blue(this) + Color.blue(color)) / 2
    return Color.rgb(red, green, blue)
}

fun Int.colorAlpha(alpha: Float): Int {
    val a = if (alpha in 0f..1f) {
        (Color.alpha(this) * alpha).toInt()
    } else {
        255
    }
    return Color.argb(a, Color.red(this), Color.green(this), Color.blue(this))
}

/**
 * List 切割
 * 平均分配
 */
fun <T> List<T>.averageAssignFixLength(splitItemNum: Int): List<List<T>> {
    val result = ArrayList<List<T>>()
    if (this.run { isNotEmpty() } && splitItemNum > 0) {
        if (this.size <= splitItemNum) {
            // 源List元素数量小于等于目标分组数量
            result.add(this)
        } else {
            // 计算拆分后list数量
            val splitNum =
                if (this.size % splitItemNum == 0) this.size / splitItemNum else this.size / splitItemNum + 1

            var value: List<T>? = null
            for (i in 0 until splitNum) {
                value = if (i < splitNum - 1) {
                    this.subList(i * splitItemNum, (i + 1) * splitItemNum)
                } else {
                    // 最后一组
                    this.subList(i * splitItemNum, this.size)
                }
                result.add(value)
            }
        }
    }
    return result
}

/**
 * 标准歌手数组转文本
 * @return 文本
 */
fun List<ArtistsData>.parse(): String {
    var artist = ""
    for (artistName in 0..this.lastIndex) {
        if (artistName != 0) {
            artist += " / "
        }
        artist += this[artistName].name
    }
    return artist
}

/**
 * @param offset 顶部偏移量
 */
fun RecyclerView.findFirstVisibleItemPosition(offset: Int = 0): Int {
    return when (val layoutManager = layoutManager) {
        is LinearLayoutManager -> {
            if (offset == 0) {
                layoutManager.findFirstVisibleItemPosition()
            } else {
                val first = layoutManager.findFirstVisibleItemPosition()
                var offsetFirstVisible = first
                for (index in first until layoutManager.itemCount) {
                    layoutManager.findViewByPosition(index)?.let {
                        if (it.top <= offset && it.bottom >= offset) {
                            offsetFirstVisible = index
                            return offsetFirstVisible
                        }
                    }
                }
                offsetFirstVisible
            }
        }
        is StaggeredGridLayoutManager -> {
            if (offset == 0) {
                layoutManager.findFirstVisibleItemPositions(null).firstOrNull() ?: 0
            } else {
                val first = layoutManager.findFirstVisibleItemPositions(null).firstOrNull() ?: 0
                var offsetFirstVisible = first
                for (index in first until layoutManager.itemCount) {
                    layoutManager.findViewByPosition(index)?.let {
                        if (it.top <= offset && it.bottom >= offset) {
                            offsetFirstVisible = index
                            return offsetFirstVisible
                        }
                    }
                }
                offsetFirstVisible
            }
        }
        else -> {
            0
        }
    }
}

fun RecyclerView.findLastVisibleItemPosition(): Int {
    return when (val layoutManager = layoutManager) {
        is LinearLayoutManager -> {
            layoutManager.findLastVisibleItemPosition()
        }
        is StaggeredGridLayoutManager -> {
            layoutManager.findLastVisibleItemPositions(null).firstOrNull() ?: 0
        }
        else -> {
            0
        }
    }
}

fun RecyclerView.scrollToPosition(position: Int?, offset: Int = 0) {
    if (null == position || position < 0) {
        return
    }
    stopScroll()
    when (val layoutManager = layoutManager) {
        is LinearLayoutManager -> {
            layoutManager.scrollToPositionWithOffset(position, offset)
        }
        is StaggeredGridLayoutManager -> {
            layoutManager.scrollToPositionWithOffset(position, offset)
        }
        else -> {
            // only support LinearLayoutManager & StaggeredGridLayoutManager
        }
    }
}