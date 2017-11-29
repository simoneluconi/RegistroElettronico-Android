package com.sharpdroid.registroelettronico.views.timetable

import android.content.Context
import android.view.View

class Divider(context: Context, val mode: Int, var thick: Boolean) : View(context) {

    init {
        if (thick) setBackgroundColor(thick_color) else setBackgroundColor(thin_color)
    }

    companion object {
        const val VERTICAL = 1
        const val HORIZONTAL = 2

        private const val thick_color = -986896
        private const val thin_color = -657931
    }
}