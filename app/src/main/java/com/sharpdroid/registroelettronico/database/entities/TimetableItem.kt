package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class TimetableItem(
        @PrimaryKey(autoGenerate = true) var id: Long,
        var profile: Int,
        var start: Float,
        var end: Float,
        var dayOfWeek: Int,
        var subject: Long,
        var color: Int
)
