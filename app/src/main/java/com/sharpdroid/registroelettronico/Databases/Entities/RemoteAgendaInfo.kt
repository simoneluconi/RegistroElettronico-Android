package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.dsl.Table

@Table
data class RemoteAgendaInfo(
        val id: Long,
        var completed: Boolean,
        var archived: Boolean
) {
    constructor() : this(0, false, false)
}