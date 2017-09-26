package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord
import com.orm.dsl.Column
import com.orm.dsl.Unique

data class Profile(
        @Unique() var username: String = "",
        @Column(name = "name", notNull = false) var name: String?,
        @Column(name = "class", notNull = false) var `class`: String?
) : SugarRecord() {

    fun getCookie(): Cookie? {
        return SugarRecord.findById(Cookie::class.java, arrayOf(username))?.getOrNull(0)
    }

    fun getTeachers(): List<Teacher> {
        return SugarRecord.find(SubjectTeacher::class.java, "username = ?", username)?.map { it?.teacher!! } ?: emptyList()
    }

    fun getSubjects(): List<Subject> {
        return SugarRecord.find(SubjectTeacher::class.java, "username = ?", username)?.map { it?.subject!! } ?: emptyList()
    }

    fun getEvents() {

    }

    fun getAbsences() {

    }

    fun getNotes() {

    }

    fun getFolders() {

    }
}