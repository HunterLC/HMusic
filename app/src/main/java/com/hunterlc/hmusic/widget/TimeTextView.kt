package com.hunterlc.hmusic.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.hunterlc.hmusic.R
import com.hunterlc.hmusic.util.TimeUtil
import com.hunterlc.hmusic.util.dp2px

class TimeTextView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val TEXT_SIZE = 12F
    }

    private var text = "00:00"
    private var align = Paint.Align.LEFT

    private val textPaint = Paint().apply {
        isAntiAlias = true
        textSize = dp2px(TEXT_SIZE).toFloat()
        color = ContextCompat.getColor(this@TimeTextView.context, R.color.colorTextForeground)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        textPaint.textAlign = align
        var x = 0f
        if (textPaint.textAlign == Paint.Align.RIGHT) {
            x = width.toFloat()
        }
        canvas?.drawText(text, x, dp2px(TEXT_SIZE).toFloat(), textPaint)
    }

    fun setText(newProcess: Int) {
        text = TimeUtil.parseDuration(newProcess)
        invalidate()
    }

    fun setAlignRight() {
        align = Paint.Align.RIGHT
    }

}