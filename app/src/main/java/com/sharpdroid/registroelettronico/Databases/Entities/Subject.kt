package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Ignore
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
data class Subject(
        @Expose @SerializedName("id") val myId: Int,
        @Expose @SerializedName("description") @Unique() val description: String,
        @Expose @SerializedName("teachers") @Ignore() var teachers: List<Teacher>?
) : SugarRecord()

data class SubjectAPI(@Expose @SerializedName("subject") val subjects: List<Subject>)