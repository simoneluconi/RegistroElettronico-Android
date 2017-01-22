package com.sharpdroid.registroelettronico.Views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CSwipeRefreshLayout extends SwipeRefreshLayout {
    private int mTouchSlop;
    private float mPrevx;
    private float mPrevy;

    public CSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public CSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevx = MotionEvent.obtain(ev).getX();
                mPrevy = MotionEvent.obtain(ev).getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float evX = ev.getX();
                final float evy = ev.getY();
                float xDiff = Math.abs(evX - mPrevx);
                float yDiff = Math.abs(evy - mPrevy);
                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
