package com.sharpdroid.registroelettronico.database.entities

import com.orm.SugarRecord

data class SubjectTeacher(
        val subject: Long,
        val teacher: Long,
        var profile: Long
) : SugarRecord() {
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