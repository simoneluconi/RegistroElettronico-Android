package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.ColumnInfo

class AverageType(
        @ColumnInfo(name = "SUM") var sum: Float,
        @ColumnInfo(name = "M_TYPE") var type: String,
        @ColumnInfo(name = "COUNT") var count: Int) {

    fun avg() = if (count > 0) sum / count else count.toFloat()
}