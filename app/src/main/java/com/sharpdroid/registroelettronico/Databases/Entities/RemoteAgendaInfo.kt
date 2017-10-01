package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.dsl.Table
import com.orm.dsl.Unique

@Table
data class RemoteAgendaInfo(
        @Unique val id: Long,
        var completed: Boolean,
        var archived: Boolean
) {
    constructor() : this(0, false, false)
}