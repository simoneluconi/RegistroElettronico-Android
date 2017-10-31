package com.sharpdroid.registroelettronico.database.entities

import android.content.Context
import android.util.SparseArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Ignore
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.utils.Account
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
@Table
data class Subject(
        @Expose @SerializedName("id") @Unique val id: Long,
        @Expose @SerializedName("description") var description: String,
        @Expose @SerializedName("teachers") @Ignore var teachers: List<Teacher>
) : Serializable {
    constructor() : this(0, "", emptyList())

    fun getInfo(c: Context): SubjectInfo {
        return SugarRecord.find(SubjectInfo::class.java, "ID=? LIMIT 1", Account.with(c).user.toString() + id.toString()).getOrNull(0) ?:
                SubjectInfo((Account.with(c).user.toString() + "" + id.toString()).toLong(), 0f, "", "", "", this, Account.with(c).user)
    }

    companion object {
        private val subjectCache = SparseArray<Subject>()

        fun clearCache() = subjectCache.clear()

        fun setupCache() {
            val subjects: Iterator<Subject> = SugarRecord.findAll(Subject::class.java)!!
            subjects.forEach {
                subjectCache.put(it.id.toInt(), it)
            }
        }
    }

    fun subject(id: Number) = subjectCache[id.toInt(), null]
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