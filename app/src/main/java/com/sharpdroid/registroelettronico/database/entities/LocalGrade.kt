package com.sharpdroid.registroelettronico.database.entities

import com.orm.SugarRecord

data class LocalGrade(
        var value: Float,
        var value_name: String,
        var subject: Long,
        var period: Int,
        var type: String,
        var profile: Long
) : SugarRecord() {
    constructor() : this(0f, "", 0, 1, "", 0)
}