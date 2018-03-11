package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import com.sharpdroid.registroelettronico.R
import com.sharpdroid.registroelettronico.database.entities.TimetableItem
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.utils.CustomTypefaceSpan
import com.sharpdroid.registroelettronico.utils.Metodi.dp

class TimetableItemView(context: Context, var item: TimetableItem, private val subjectPOJO: SubjectPOJO?) : AppCompatTextView(context) {
    private val padding = dp(4)

    init {
        setTextColor(Color.WHITE)
        setPadding(padding, padding, padding, padding)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

        getRoundedRectangle(item.color).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                background = it
            } else {
                setBackgroundDrawable(it)
            }
        }

        val spannable = SpannableString(subjectPOJO?.getSubjectName().orEmpty() + "\n" + item.where.orEmpty())
        spannable.setSpan(CustomTypefaceSpan("", Typeface.create("sans-serif-medium", Typeface.NORMAL)), 0, subjectPOJO?.getSubjectName().orEmpty().length, 0)
        spannable.setSpan(CustomTypefaceSpan("", Typeface.create("sans-serif-regular", Typeface.NORMAL)), spannable.length - (item.where.orEmpty().length), spannable.length, 0)
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#DDDDDD")), spannable.length - (item.where.orEmpty().length), spannable.length, 0)
        text = spannable
    }

    private fun getRoundedRectangle(color: Int): Drawable {
        val corners = dp(2).toFloat()
        val shape = ShapeDrawable(RoundRectShape(floatArrayOf(corners, corners, corners, corners, corners, corners, corners, corners), null, null))

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val states = arrayOf(intArrayOf())
            val colors = intArrayOf(0xaaffffff.toInt())
            shape.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            RippleDrawable(ColorStateList(states, colors), shape, null)
        } else {
            val dr = ContextCompat.getDrawable(context, R.drawable.timetable_item)
            dr.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            dr
        }
    }
}