package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "EXCLUDED_MARKS")
data class ExcludedMark(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "ID")
        var id: Long = 0L
)