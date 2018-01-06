package com.sharpdroid.registroelettronico.views.cells

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.pojos.GradeWithSubjectName
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import kotlinx.android.synthetic.main.adapter_mark.view.*

@Suppress("DEPRECATION")
class GradeCell : RelativeLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        View.inflate(context, R.layout.adapter_mark, this)
        with(getChildAt(0)) {
            isClickable = false
            isFocusable = false
            setOnClickListener(null)
            setBackgroundDrawable(null)
            setPadding(0, dp(16), 0, dp(16))
        }
    }

    fun bind(grade: GradeWithSubjectName, target: Float) {

        color.setImageDrawable(ColorDrawable(ContextCompat.getColor(context, Metodi.getMarkColor(if (grade.grade.isExcluded()) 0f else grade.grade.mValue, target))))
        mark.text = grade.grade.mStringValue

        content.text = grade.grade.mNotes.trim { it <= ' ' }

        content.visibility = if (TextUtils.isEmpty(content.text)) View.GONE else View.VISIBLE

        type.text = grade.grade.mType
        date.text = capitalizeEach(grade.subject.orEmpty())
    }
}