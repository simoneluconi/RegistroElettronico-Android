package com.sharpdroid.registroelettronico.Utils

import android.os.Build
import android.support.annotation.StyleRes
import android.widget.TextView
import java.util.*

fun TextView.setTextAppearanceCompat(@StyleRes style: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(style)
    } else {
        setTextAppearance(context, style)
    }
}

fun String?.or(s: String): String = if (isNullOrEmpty()) s else this!!

fun Date.flat(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    return cal.time
}