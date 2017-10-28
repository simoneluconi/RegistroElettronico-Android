package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView

import com.sharpdroid.registroelettronico.utils.LayoutHelper
import com.sharpdroid.registroelettronico.utils.Metodi

class ValueDetailsCheckboxCell(context: Context) : FrameLayout(context) {
    private var textView: TextView
    private var valueTextView: TextView
    private var checkBox: CheckBox
    private var dividerPaint: Paint = Paint()
    private var needDivider: Boolean = false

    var isChecked: Boolean
        get() = checkBox.isChecked
        set(checked) {
            checkBox.isChecked = checked
        }

    init {
        dividerPaint.strokeWidth = 1f
        dividerPaint.color = -0x262627

        checkBox = CheckBox(context)
        addView(checkBox, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.START or Gravity.CENTER_VERTICAL, 8f, 0f, 16f, 0f))

        textView = TextView(context)
        textView.setTextColor(Color.BLACK)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
        textView.setLines(1)
        textView.maxLines = 1
        textView.setSingleLine(true)
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        addView(textView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.START or Gravity.TOP, 57f, 10f, 17f, 0f))

        valueTextView = TextView(context)
        valueTextView.setTextColor(-0x757576)
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        valueTextView.gravity = Gravity.START
        valueTextView.setLines(1)
        valueTextView.maxLines = 1
        valueTextView.setSingleLine(true)
        valueTextView.setPadding(0, 0, 0, 0)
        addView(valueTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.START or Gravity.TOP, 57f, 35f, 17f, 0f))
    }

    fun setTextAndValue(text: String, value: String, divider: Boolean) {
        textView.visibility = View.VISIBLE
        valueTextView.visibility = View.VISIBLE
        textView.text = text
        valueTextView.text = value
        needDivider = divider
        setWillNotDraw(!divider)
    }

    fun setText(text: String, divider: Boolean) {
        textView.visibility = View.VISIBLE
        valueTextView.visibility = View.GONE
        textView.text = text
        needDivider = divider
        setWillNotDraw(!divider)
    }

    override fun onDraw(canvas: Canvas) {
        if (needDivider) {
            canvas.drawLine(paddingLeft.toFloat(), (height - 1).toFloat(), (width - paddingRight).toFloat(), (height - 1).toFloat(), dividerPaint)
        }
    }

    fun setCheckBoxListener(l: View.OnClickListener) {
        checkBox.setOnClickListener(l)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(Metodi.dp(64) + if (needDivider) 1 else 0, View.MeasureSpec.EXACTLY))

    }
}
