package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo

data class Average(
        @ColumnInfo(name = "NAME") var name: String = "",
        @ColumnInfo(name = "CODE") var code: Int = 0,
        @ColumnInfo(name = "AVG") var avg: Float = 0f,
        @ColumnInfo(name = "TARGET") var target: Float = 0f,
        @ColumnInfo(name = "COUNT") var count: Int = 0
)
