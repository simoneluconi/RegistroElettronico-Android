package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.util.SparseArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "TEACHER")
class Teacher(
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("teacherId") var id: Long = 0L,
        @ColumnInfo(name = "TEACHER_NAME") @Expose var teacherName: String = "",
        @Ignore @Expose var folders: List<Folder> = emptyList()
) : Serializable {
    companion object {
        private val teachersOfSubject = SparseArray<List<Teacher>>()
        private val teacherWithID = SparseArray<Teacher>()

        fun clearCache() {
            teachersOfSubject.clear()
            teacherWithID.clear()
        }

        fun setupCache() {/*
            val teachers: List<Teacher> = SugarRecord.find(Teacher::class.java, "")!!
            val subjectTeacher = SubjectTeacher.cache

            subjectTeacher.forEach {
                teachersOfSubject.put(it.subject.toInt(), teachers.filter { teacher -> it.teacher == teacher.id })
            }

            teachers.forEach {
                teacherWithID.put(it.id.toInt(), it)
            }*/
        }

        fun professorsOfSubject(code: Number): List<Teacher> = teachersOfSubject[code.toInt(), emptyList()]
        fun teacher(id: Number): Teacher? = teacherWithID[id.toInt(), null]
    }
}

class DidacticAPI(@Expose @SerializedName("didacticts") val didactics: List<Teacher>)