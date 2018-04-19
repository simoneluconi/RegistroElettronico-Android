package com.sharpdroid.registroelettronico.views.localEvent

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.view_event_option.view.*

class OptionView : RelativeLayout {
    private var onClickListener: ((View) -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.OptionView,
                0, 0)

        try {
            setTitle(a.getString(R.styleable.OptionView_title))
            setContent(a.getString(R.styleable.OptionView_content_view))
            setImage(a.getDrawable(R.styleable.OptionView_image))
        } finally {
            a.recycle()
        }
    }

    fun builder() = Builder(context)

    private fun init() {
        View.inflate(context, R.layout.view_event_option, this)
    }

    fun setTitle(t: String) {
        title.text = t
    }

    fun setContent(c: String?) {
        if (c.isNullOrEmpty()) {
            content.text = context.getString(R.string.not_set)
        } else {
            content.text = c
        }

    }

    fun setImage(@DrawableRes drawable: Int) {
        image.setImageResource(drawable)
    }

    private fun setImage(drawable: Drawable) {
        image.setImageDrawable(drawable)
    }

    override fun setOnClickListener(onClickListener: View.OnClickListener) {
        this.onClickListener = { onClickListener.onClick(it) }
        layout.setOnClickListener(onClickListener)
    }

    class Builder(private var c: Context) {
        private var title: String? = null
        private var content: String? = null
        @DrawableRes
        private var drawable: Int = 0
        private var listener: ((View) -> Unit)? = null

        fun title(t: String): Builder {
            title = t
            return this
        }

        fun content(c: String): Builder {
            content = c
            return this
        }

        fun image(@DrawableRes i: Int): Builder {
            drawable = i
            return this
        }

        fun onClick(l: (View) -> Unit): Builder {
            listener = l
            return this
        }

        fun build(): OptionView {
            val v = OptionView(c)
            v.setImage(drawable)
            v.setContent(content)
            title?.let { v.setTitle(it) }
            listener?.let { v.setOnClickListener(it) }
            return v
        }

    }
}
