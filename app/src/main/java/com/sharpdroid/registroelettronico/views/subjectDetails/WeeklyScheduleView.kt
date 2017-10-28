package com.sharpdroid.registroelettronico.views.subjectDetails

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet

class WeeklyScheduleView : CardView {
    internal var mContext: Context

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
    }
}
