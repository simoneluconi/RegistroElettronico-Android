package com.sharpdroid.registroelettronico.views.subjectDetails

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.utils.Metodi.getMarkColor
import kotlinx.android.synthetic.main.view_target.view.*
import java.util.*

class TargetView : CardView {
    internal var mContext: Context

    var target = 1f
        internal set

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
        View.inflate(mContext, R.layout.view_target, this)
    }

    fun setTarget(new_media: Float, target: Float, animate: Boolean) {
        this.target = target


        if (target == -1f || new_media == 0f) {
            obiettivo.text = "-"
        } else {
            obiettivo.text = String.format(Locale.getDefault(), "%.2f", target)
        }


        if (new_media != 0f) {
            media.text = String.format(Locale.getDefault(), "%.2f", new_media)

            if (animate) {
                animateBar(target, new_media, getColor(getMarkColor(new_media, target)))
            } else {
                progress.progressColor = getColor(getMarkColor(new_media, target))
                progress.max = target
                progress.progress = new_media
            }
        } else {
            progress.progressColor = getColor(R.color.intro_blue)
            progress.max = 10f
            progress.progress = 11f
            media.text = "-"
        }
    }

    private fun animateBar(target: Float, media: Float, color: Int) {
        ObjectAnimator.ofFloat(progress, "max", target).start()
        ObjectAnimator.ofFloat(progress, "progress", media).start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ObjectAnimator.ofArgb(progress, "progressColor", color).start()
        } else {
            progress.progressColor = color
        }
    }

    fun setButtonsListener(target: OnClickListener, details: OnClickListener) {
        imposta.setOnClickListener(target)
        dettagli.setOnClickListener(details)
    }

    private fun getColor(color: Int): Int {
        return ContextCompat.getColor(mContext, color)
    }
}
