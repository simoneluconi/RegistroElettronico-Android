package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.util.SparseArray
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
data class Subject(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("id") var id: Long = 0L,
        @ColumnInfo(name = "DESCRIPTION") @Expose @SerializedName("description") var description: String = "",
        @Ignore @Expose @SerializedName("teachers") var teachers: List<Teacher>
) : Serializable {
    constructor() : this(0, "", emptyList())

    fun getInfo(c: Context): SubjectInfo {
        throw IllegalStateException("use proper DAO")
        //return SugarRecord.find(SubjectInfo::class.java, "ID=? LIMIT 1", Account.with(c).user.toString() + id.toString()).getOrNull(0) ?:
        //      SubjectInfo((Account.with(c).user.toString() + "" + id.toString()).toLong(), 0f, "", "", "", this, Account.with(c).user)
    }

    companion object {
        private val subjectCache = SparseArray<Subject>()

        fun clearCache() = subjectCache.clear()

        fun setupCache() {
            throw IllegalStateException("use proper DAO")
            /*
            val subjects: Iterator<Subject> = SugarRecord.findAll(Subject::class.java)!!
            subjects.forEach {
                subjectCache.put(it.id.toInt(), it)
            }*/
        }

        fun subject(id: Number) = subjectCache[id.toInt(), null]
    }
}


@Entity
data class SubjectInfo(
        @ColumnInfo(name = "ID") @PrimaryKey(autoGenerate = true) var id: Long = 0L,
        @ColumnInfo(name = "TARGET") var target: Float,
        @ColumnInfo(name = "DESCRIPTION") var description: String = "",
        @ColumnInfo(name = "DETAILS") var details: String = "",
        @ColumnInfo(name = "CLASSROOM") var classroom: String = "",
        @ColumnInfo(name = "SUBJECT") var subject: Subject,
        @ColumnInfo(name = "PROFILE") var profile: Long)

data class SubjectAPI(@Expose @SerializedName("subjects") val subjects: List<Subject>)