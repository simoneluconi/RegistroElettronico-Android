package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord
import com.orm.dsl.Column

data class Cookie(
        @Column(name = "id") val username: String,
        var key: String = "",
        var value: String = ""
) : SugarRecord()