package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo

class Average(
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "M_SUBJECT_ID") var code: Int = 0,
        @ColumnInfo(name = "SUM") var sum: Float = 0f,
        @ColumnInfo(name = "TARGET") var target: Float = 0f,
        @ColumnInfo(name = "COUNT") var count: Int = 0
)
