package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "LOCAL_GRADE")
data class LocalGrade(
        @ColumnInfo(name = "VALUE") var value: Float,
        @ColumnInfo(name = "VALUE_NAME") var value_name: String = "",
        @ColumnInfo(name = "SUBJECT") var subject: Long = -1L,
        @ColumnInfo(name = "PERIOD") var period: Int = -1,
        @ColumnInfo(name = "TYPE") var type: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L,
        @android.arch.persistence.room.Ignore @Ignore var index: Int
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    @Ignore
    var id = -1L

    constructor() : this(0f, "", 0, 1, "", 0, 0)
}