package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import com.sharpdroid.registroelettronico.utils.Metodi.capitalizeEach
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import com.sharpdroid.registroelettronico.utils.or
import io.reactivex.android.schedulers.AndroidSchedulers

class TimetableItemView : TextView {
    private val padding = dp(4)
    var item: TimetableItem? = null
        set(value) {
            field = value
            DatabaseHelper.database.subjectsDao().getSubjectInfoSingle(value!!.subject).observeOn(AndroidSchedulers.mainThread()).subscribe { t ->
                text = capitalizeEach(t.subjectInfo.getOrNull(0)?.description.or(t.subject.description), false)
            }
        }

    init {
        setTextColor(Color.WHITE)
        setPadding(padding, padding, padding, padding)
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setBackgroundResource(R.drawable.timetable_item)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}