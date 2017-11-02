package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Table
@Entity(tableName = "OPTION")
data class Option(
        @ColumnInfo(name = "ID") @PrimaryKey @Unique var id: Long = -1L,
        @ColumnInfo(name = "NOTIFY") var notify: Boolean = false,
        @ColumnInfo(name = "NOTIFY_AGENDA") var notifyAgenda: Boolean = false,
        @ColumnInfo(name = "NOTIFY_VOTI") var notifyVoti: Boolean = false,
        @ColumnInfo(name = "NOTIFY_NOTE") var notifyNote: Boolean = false,
        @ColumnInfo(name = "NOTIFY_COMUNICAZIONI") var notifyComunicazioni: Boolean) {
    constructor() : this(-1L, false, false, false, false, false)
}