package com.ai.groupchattest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout


class GroupChatView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private val participants = HashMap<Int, Participant>()
    private var self: Participant? = null

    private var itemWidth: Int
    private var itemHeight: Int
    private var animationDuration: Long

    init {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.GroupChatView, 0, 0)
        itemWidth = array.getDimensionPixelSize(R.styleable.GroupChatView_itemWidth, 0)
        itemHeight = array.getDimensionPixelSize(R.styleable.GroupChatView_itemHeight, 0)
        animationDuration =
            array.getInteger(R.styleable.GroupChatView_animationDuration, 0).toLong()
        array.recycle()
    }

    fun addParticipant(id: Int, x: Int, y: Int, isSelf: Boolean = false): ParticipantTextureView? {
        val participant = Participant(id, x, y)
        if (!participants.containsKey(id)) {
            participants[id] = participant
            if (isSelf) self = participant
            val roundedParent = RoundedFrameLayout(context)
            roundedParent.tag = participant
            if (isSelf) roundedParent.elevation = 1f
            roundedParent.layoutParams = LayoutParams(itemWidth, itemHeight)
            val textureView = ParticipantTextureView(context)
            textureView.layoutParams = LayoutParams(itemWidth, itemHeight)
            roundedParent.addView(textureView)
            addView(roundedParent)
            return textureView
        } else {
            throw IllegalStateException("Participant already exists")
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val participant = child.tag as Participant?
            if (participant != null) {
                val lp = child.layoutParams as LayoutParams
                val l = left + participant.x + lp.leftMargin
                val t = top + participant.y + lp.topMargin
                val w = child.measuredWidth
                val h = child.measuredHeight
                child.layout(l - w / 2, t - h / 2, l + w / 2, t + h / 2)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            moveSelfParticipant(event.x.toInt(), event.y.toInt())
        }
        return true
    }

    private fun moveSelfParticipant(x: Int, y: Int) {
        self?.also { self ->
            val view = findViewWithTag<View>(self)
            val deltaX = (x - self.x).toFloat()
            val deltaY = (y - self.y).toFloat()
            view.animate()
                .translationX(deltaX)
                .translationY(deltaY)
                .setDuration(animationDuration)
                .start()
        }
    }

    private data class Participant(val id: Int, val x: Int, val y: Int)
}