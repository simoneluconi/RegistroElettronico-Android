package com.sharpdroid.registroelettronico.Databases.Entities

import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Ignore
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Utils.Account

/*
{
    "subjectInfo": [
        {
            "id": 214401,
            "description": "STORIA",
            "order": 1,
            "teachers": [
                {
                    "teacherId": "A3446245",
                    "teacherName": "RAGONE ROSARIO"
                }
            ]
        }
    ]
}
 */
@Table
data class Subject(
        @Expose @SerializedName("id") @Unique val id: Long,
        @Expose @SerializedName("description") var description: String,
        @Expose @SerializedName("teachers") @Ignore var teachers: List<Teacher>
) {
    constructor() : this(0, "", emptyList())

    fun getInfo(c: Context): SubjectInfo? {
        return SugarRecord.find(SubjectInfo::class.java, "ID=? LIMIT 1", Account.with(c).user.toString() + id.toString()).getOrNull(0)
    }
}

@Table
data class SubjectInfo(
        @Unique var id: Long,
        var target: Float,
        var description: String,
        var details: String,
        var classroom: String,
        var subject: Subject,
        var profile: Long) {
    constructor() : this(0L, 0f, "", "", "", Subject(), -1L)
}

data class SubjectAPI(@Expose @SerializedName("subjects") val subjects: List<Subject>)