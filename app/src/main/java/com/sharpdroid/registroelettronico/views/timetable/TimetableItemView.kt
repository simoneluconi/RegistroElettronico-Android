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
import com.sharpdroid.registroelettronico.utils.Metodi.dp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class TimetableItemView : TextView {
    private val padding = dp(4)
    private var disposable: Disposable? = null

    var item: TimetableItem? = null
        set(value) {
            field = value
            if (disposable == null) {
                disposable = DatabaseHelper.database.subjectsDao().getSubjectInfoFlowable(value!!.subject).observeOn(AndroidSchedulers.mainThread()).subscribe { it ->
                    val t = it.getOrNull(0)
                    t?.getSubjectName()?.let {
                        text = it
                    }
                }
            }
        }

    init {
        setTextColor(Color.WHITE)
        setPadding(padding, padding, padding, padding)
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setBackgroundResource(R.drawable.timetable_item)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable?.dispose()
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}