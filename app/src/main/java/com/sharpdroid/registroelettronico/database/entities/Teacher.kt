package com.sharpdroid.registroelettronico.database.entities

import android.util.SparseArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Ignore
import com.orm.dsl.Table
import com.orm.dsl.Unique
import java.io.Serializable

@Table
data class Teacher(
        @Expose @Unique @SerializedName("teacherId") var id: Long,
        @Expose var teacherName: String,
        @Expose @Ignore var folders: List<Folder> //not present in /subjects
) : Serializable {
    constructor() : this(0, "", emptyList())

    companion object {
        private val teachersOfSubject = SparseArray<List<Teacher>>()

        fun clearCache() = teachersOfSubject.clear()

        fun setupCache() {
            if (SubjectTeacher.cache.isEmpty()) throw IllegalStateException("You need to inizialize SubjectTeacher's cache before Teacher's")

            val teachers: List<Teacher> = SugarRecord.find(Teacher::class.java, "")!!
            val subjectTeacher = SubjectTeacher.cache

            subjectTeacher.forEach {
                teachersOfSubject.put(it.subject.toInt(), teachers.filter { teacher -> it.teacher == teacher.id })
            }
        }

        fun professorsOfSubject(code: Number): List<Teacher> = teachersOfSubject[code.toInt(), emptyList()]
    }
}

data class DidacticAPI(@Expose @SerializedName("didacticts") val didactics: List<Teacher>)