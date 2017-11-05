package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgendaInfo

data class RemoteAgendaPOJO(
        @Embedded var event: RemoteAgenda,
        @Relation(parentColumn = "ID", entityColumn = "ID") var info: List<RemoteAgendaInfo>
) {
    fun isTest(): Boolean {
        return event.isTest(info.getOrNull(0))
    }

    fun isCompleted(): Boolean {
        return info.getOrNull(0)?.completed == true
    }

    constructor() : this(RemoteAgenda(), emptyList())
}