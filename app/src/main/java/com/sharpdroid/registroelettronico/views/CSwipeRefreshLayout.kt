package com.sharpdroid.registroelettronico.views

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration

class CSwipeRefreshLayout(context: Context, attrs: AttributeSet? = null) : SwipeRefreshLayout(context, attrs) {
    private val mTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var mPrevX: Float = 0f
    private var mPrevY: Float = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = MotionEvent.obtain(ev).x
                mPrevY = MotionEvent.obtain(ev).y
            }
            MotionEvent.ACTION_MOVE -> {
                val evX = ev.x
                val evy = ev.y
                val xDiff = Math.abs(evX - mPrevX)
                val yDiff = Math.abs(evy - mPrevY)
                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}
