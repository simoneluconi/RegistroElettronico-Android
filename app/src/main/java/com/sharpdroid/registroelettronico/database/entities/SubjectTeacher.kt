package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "SUBJECT_TEACHER")
data class SubjectTeacher(
        @ColumnInfo(name = "SUBJECT") var subject: Long = -1L,
        @ColumnInfo(name = "TEACHER") var teacher: Long = -1L,
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    @PrimaryKey(autoGenerate = true)
    @Ignore
    @ColumnInfo(name = "ID")
    var id = -1L

    constructor() : this(-1L, -1L, -1L)

    companion object {
        val cache = ArrayList<SubjectTeacher>()

        fun clearCache() {
            cache.clear()
        }

        fun setupCache(account: Long) {
            val subjectsTeachers = SugarRecord.find(SubjectTeacher::class.java, "PROFILE=$account")
            subjectsTeachers.forEach {
                cache.add(it)
            }
        }

    }
}