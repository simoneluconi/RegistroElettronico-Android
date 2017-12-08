package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import kotlinx.android.synthetic.main.view_complex.view.*

class ComplexCell : FrameLayout {
    init {
        View.inflate(context, R.layout.view_complex, this)
        isClickable = true
        isFocusable = true
    }

    fun setup(title: String, drawable: Drawable?, listener: ((View) -> Unit)?) {
        textView.text = title

        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        setOnClickListener(listener)
    }

    fun setup(title: String, content: String, drawable: Drawable?, listener: ((View) -> Unit)?) {
        val spannable = SpannableString(title + "\n" + content)
        spannable.setSpan(TextAppearanceSpan(context, R.style.TextAppearance_AppCompat_Body1), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(TextAppearanceSpan(context, R.style.TextAppearance_AppCompat_Caption), title.length + 1, title.length + 1 + content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable

        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        textView.compoundDrawablePadding = if (drawable == null) 0 else dp(16)
        setOnClickListener(listener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), MeasureSpec.getSize(heightMeasureSpec))
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}