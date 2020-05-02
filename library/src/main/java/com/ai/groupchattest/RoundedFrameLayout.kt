package com.ai.groupchattest

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.math.min

class RoundedFrameLayout(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private val path = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = w / 2f
        val centerY = h / 2f
        path.reset()
        path.addCircle(centerX, centerY, min(centerX, centerY), Path.Direction.CW)
        path.close()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save: Int = canvas.save()
        canvas.clipPath(path)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }
}