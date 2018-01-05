package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
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

    fun setup(title: String, drawable: Drawable, color: Boolean, listener: ((View) -> Unit)?) {
        textView.text = title

        if (color)
            drawable.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        textView.compoundDrawablePadding = dp(40) - drawable.intrinsicWidth
        setOnClickListener(listener)
    }

    fun setup(title: String, content: String, drawable: Drawable?, listener: ((View) -> Unit)?) {
        val spannable = SpannableString(title + "\n" + content)
        spannable.setSpan(TextAppearanceSpan(context, R.style.TextAppearance_AppCompat_Body1), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(TextAppearanceSpan(context, R.style.TextAppearance_AppCompat_Caption), title.length + 1, title.length + 1 + content.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable

        drawable?.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        setOnClickListener(listener)
    }

    fun setup(title: String, content: String, drawable: Drawable?, errorText: String, isError: Boolean, listener: ((View) -> Unit)?) {
        val spannable = SpannableString(title + "\n" + if (!isError) content else errorText)
        spannable.setSpan(TextAppearanceSpan(context, R.style.TextAppearance_AppCompat_Body1), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(TextAppearanceSpan(context, if (!isError) R.style.TextAppearance_AppCompat_Caption else R.style.TextAppearance_Design_Error), title.length + 1, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannable
        if (drawable != null) {
            drawable.setColorFilter(0xff636363.toInt(), PorterDuff.Mode.SRC_ATOP)
            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        } else {
            val oval = ShapeDrawable(OvalShape())
            oval.intrinsicWidth = dp(24)
            oval.intrinsicHeight = dp(24)
            oval.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            textView.setCompoundDrawablesWithIntrinsicBounds(oval, null, null, null)
        }
        setOnClickListener(listener)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), MeasureSpec.getSize(heightMeasureSpec))
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}