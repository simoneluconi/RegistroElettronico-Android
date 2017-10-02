package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Ignore
import com.orm.dsl.Table
import com.orm.dsl.Unique

/*
{
    "subject": [
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
        @Expose @SerializedName("teachers") @Ignore var teachers: List<Teacher>,
        var target: Float,
        var classroom: String,
        var details: String
) {
    constructor() : this(0, "", emptyList(), 0f, "", "")
}

data class SubjectAPI(@Expose @SerializedName("subjects") val subjects: List<Subject>)