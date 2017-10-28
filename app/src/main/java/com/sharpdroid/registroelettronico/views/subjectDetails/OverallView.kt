package com.sharpdroid.registroelettronico.views.subjectDetails

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.view_overall_subject.view.*
import java.util.*

class OverallView : CardView {
    internal var mContext: Context

    constructor(context: Context) : super(context) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        init()
    }

    internal fun init() {
        View.inflate(mContext, R.layout.view_overall_subject, this)
    }

    fun setScritto(Scritto: Float?) {
        if (Scritto == null)
            scritto.text = "-"
        else
            scritto.text = String.format(Locale.getDefault(), "%.2f", Scritto)
    }

    fun setOrale(Orale: Float?) {
        if (Orale == null)
            orale.text = "-"
        else
            orale.text = String.format(Locale.getDefault(), "%.2f", Orale)
    }

    fun setPratico(Pratico: Float?) {
        if (Pratico == null)
            pratico.text = "-"
        else
            pratico.text = String.format(Locale.getDefault(), "%.2f", Pratico)
    }
}
