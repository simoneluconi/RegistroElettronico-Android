package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class TimetableItem(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        var profile: Int = -1,
        var start: Float = 0f,
        var end: Float = 0f,
        var dayOfWeek: Int = 0,
        var subject: Long = 0,
        var color: Int = 0,
        var where: String? = null,
        var notes: String? = null
)
