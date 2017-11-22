package com.sharpdroid.registroelettronico.utils

import android.widget.FrameLayout

object LayoutHelper {

    val MATCH_PARENT = -1
    val WRAP_CONTENT = -2

    private fun getSize(size: Int) = if (size < 0) size else Metodi.dp(size)

    fun createFrame(width: Int, height: Int, gravity: Int, leftMargin: Float, topMargin: Float, rightMargin: Float, bottomMargin: Float): FrameLayout.LayoutParams {
        val layoutParams = FrameLayout.LayoutParams(getSize(width), getSize(height), gravity)
        layoutParams.setMargins(Metodi.dp(leftMargin), Metodi.dp(topMargin), Metodi.dp(rightMargin), Metodi.dp(bottomMargin))
        return layoutParams
    }
}
