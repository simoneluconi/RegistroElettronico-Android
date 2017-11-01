package com.sharpdroid.registroelettronico.database.entities

import com.orm.SugarRecord
import com.orm.dsl.Ignore

data class LocalGrade(
        var value: Float,
        var value_name: String,
        var subject: Long,
        var period: Int,
        var type: String,
        var profile: Long,
        @Ignore var index: Int
) : SugarRecord() {
    constructor() : this(0f, "", 0, 1, "", 0, 0)
}