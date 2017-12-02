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
        var subject: Long
) {
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + profile
        result = 31 * result + start.hashCode()
        result = 31 * result + end.hashCode()
        result = 31 * result + dayOfWeek
        result = 31 * result + subject.hashCode()
        return result
    }
}