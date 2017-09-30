package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Ignore
import com.orm.dsl.Table

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
        @Expose @SerializedName("id") val id: Long,
        @Expose @SerializedName("description") val description: String,
        @Expose @SerializedName("teachers") @Ignore() var teachers: List<Teacher> = emptyList()
)

data class SubjectAPI(@Expose @SerializedName("subjects") val subjects: List<Subject>)