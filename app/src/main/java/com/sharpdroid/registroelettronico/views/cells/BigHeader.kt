package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.sharpdroid.registroelettronico.R
import kotlinx.android.synthetic.main.cell_header.view.*

class BigHeader : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        View.inflate(context, R.layout.cell_header, this)
    }

    fun setText(text: String) {
        textView.text = text
    }

}