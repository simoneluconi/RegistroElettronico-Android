package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.dsl.Table
import com.orm.dsl.Unique

@Table
data class Option(
        @Unique val id: Long,
        var notify: Boolean,
        var notifyAgenda: Boolean,
        var notifyVoti: Boolean,
        var notifyNote: Boolean,
        var notifyComunicazioni: Boolean) {
    constructor() : this(-1L, false, false, false, false, false)
}