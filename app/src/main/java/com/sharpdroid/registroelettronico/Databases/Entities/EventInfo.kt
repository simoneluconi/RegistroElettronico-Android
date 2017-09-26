package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord

data class EventInfo(
        var remote: Boolean,
        val eventId: Int,
        var completed: Boolean,
        var archived: Boolean
) : SugarRecord()