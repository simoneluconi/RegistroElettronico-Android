package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "SUBJECT_TEACHER")
data class SubjectTeacher(
        @ColumnInfo(name = "SUBJECT") var subject: Long = 0L,
        @ColumnInfo(name = "TEACHER") var teacher: Long = 0L,
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id = 0L

    constructor() : this(0L, 0L, 0L)

    companion object {
        val cache = ArrayList<SubjectTeacher>()

        fun clearCache() {
            cache.clear()
        }

        fun setupCache(account: Long) {
            throw IllegalStateException("use proper DAO")
            /*val subjectsTeachers = SugarRecord.find(SubjectTeacher::class.java, "PROFILE=$account")
            subjectsTeachers.forEach {
                cache.add(it)
            }*/
        }

    }
}