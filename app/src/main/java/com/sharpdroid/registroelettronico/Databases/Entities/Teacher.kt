package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord
import com.orm.dsl.Unique

data class Teacher(
        @Unique var id: Int,
        var name: String
) : SugarRecord()