package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class Average(
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "M_SUBJECT_ID") var code: Int = 0,
        @ColumnInfo(name = "SUM") var sum: Float = 0f,
        @ColumnInfo(name = "TARGET") var target: Float = 0f,
        @ColumnInfo(name = "COUNT") var count: Int = 0
) {
    fun avg() = if (count > 0) sum / count else 0f

    fun isExcluded(): Boolean {
        return DatabaseHelper.database.gradesDao().countExcluded(code.toLong()) == 1L
    }

    fun exclude(exclude: Boolean) {
        if (exclude) {
            DatabaseHelper.database.gradesDao().exclude(ExcludedMark(code.toLong()))
        } else {
            DatabaseHelper.database.gradesDao().dontExclude(ExcludedMark(code.toLong()))
        }
    }
}
