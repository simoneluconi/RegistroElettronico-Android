package com.sharpdroid.registroelettronico.database.pojos

data class GeniusTimetable(
        var dayOfWeek: Int = 0,
        var teacher: Int = 0,
        var subject: Int = 0,
        var start: Int = 0,
        var end: Int = 0
)