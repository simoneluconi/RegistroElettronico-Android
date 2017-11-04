package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

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
@Entity(tableName = "SUBJECT")
class Subject(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("id")var id: Long = 0L,
        @ColumnInfo(name = "DESCRIPTION") @Expose @SerializedName("description") var description: String = "",
        @Ignore @Expose @SerializedName("teachers") var teachers: List<Teacher> = emptyList()
) : Serializable

@Entity(tableName = "SUBJECT_INFO")
class SubjectInfo(
        @ColumnInfo(name = "ID") @PrimaryKey(autoGenerate = true) var id: Long = 0L,
        @ColumnInfo(name = "TARGET") var target: Float = 0f,
        @ColumnInfo(name = "DESCRIPTION") var description: String = "",
        @ColumnInfo(name = "DETAILS") var details: String = "",
        @ColumnInfo(name = "CLASSROOM") var classroom: String = "",
        @ColumnInfo(name = "SUBJECT") var subject: Long = 0L,
        @ColumnInfo(name = "PROFILE") var profile: Long = 0L
)

class SubjectAPI(@Expose @SerializedName("subjects") val subjects: List<Subject>)