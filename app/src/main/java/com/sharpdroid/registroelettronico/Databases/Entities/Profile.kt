package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord
import com.orm.dsl.Column
import com.orm.dsl.Unique

data class Profile(
        @Unique() var username: String,
        @Column(name = "name", notNull = false) var name: String?,
        @Column(name = "class", notNull = false) var `class`: String?,
        var cookie: Cookie,
        var remoteAgenda: List<RemoteAgenda>) : SugarRecord()