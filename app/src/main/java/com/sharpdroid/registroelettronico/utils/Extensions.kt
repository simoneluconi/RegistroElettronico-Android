@file:Suppress("DEPRECATION")

package com.sharpdroid.registroelettronico.utils

import java.util.*

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

fun Date.add(type: Int, value: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(type, value)
    return cal.time
}