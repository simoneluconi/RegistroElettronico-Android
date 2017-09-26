package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord

data class Cookie(
        var key: String,
        var value: String
) : SugarRecord()