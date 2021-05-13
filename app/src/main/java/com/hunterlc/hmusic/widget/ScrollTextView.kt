package com.hunterlc.hmusic.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.hunterlc.hmusic.util.LogUtil
import java.util.*

@SuppressLint("AppCompatCustomView")
class ScrollTextView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {
    private var mText = "蒹葭苍苍，白露为霜。所谓伊人，在水一方。"
    private var mOffsetX = 0
    private val mRect: Rect = Rect()
    private var mTimer: Timer?
    private var mTimerTask: TimerTask?

    /**
     * 速度，负数左移，正数右移。
     */
    private var mSpeed = -10

    constructor(context: Context?) : this(context, null) {}

    private inner class MyTimerTask : TimerTask() {
        override fun run() {
            //如果View能容下所有文字，直接返回
            if (mRect.right < width) {
                return
            }
            if (mOffsetX < -mRect.right - paddingEnd) {
                //左移时的情况
                mOffsetX = paddingStart
            } else if (mOffsetX > paddingStart) {
                //右移时的情况
                mOffsetX = -mRect.right
            }
            mOffsetX += mSpeed
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        //此处去掉了super.onDraw(Canvas canvas);
        mText = text.toString()
        val textPaint: TextPaint = paint
        textPaint.color = currentTextColor
        //获取文本区域大小，保存在mRect中。
        textPaint.getTextBounds(mText,0, mText.length, mRect)
        val mTextCenterVerticalToBaseLine: Float =
            (-textPaint.ascent() + textPaint.descent()) / 2 - textPaint.descent()

        if (mRect.right < width) {
            textPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(mText, width / 2F, height / 2 + mTextCenterVerticalToBaseLine, textPaint)
        } else {
            textPaint.textAlign = Paint.Align.LEFT
            canvas.drawText(
                mText,
                mOffsetX.toFloat(),
                height / 2 + mTextCenterVerticalToBaseLine,
                textPaint
            )
        }
    }

    /**
     * 视图移除时销毁任务和定时器
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LogUtil.e(TAG, "killTimer")
        if (mTimerTask != null) {
            mTimerTask!!.cancel()
            mTimerTask = null
        }
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    fun setSpeed(speed: Int) {
        mSpeed = speed
    }

    companion object {
        private const val TAG = "ScrollTextView"
        private const val PFS = 60
    }

    init {
        mTimer = Timer()
        mTimerTask = MyTimerTask()
        //更新帧率
        mTimer!!.schedule(mTimerTask, 0L, 1000L / PFS)
    }
}