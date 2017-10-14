package com.sharpdroid.registroelettronico.Databases.Entities

import com.orm.SugarRecord

data class SubjectTeacher(
        val subject: Subject,
        val teacher: Teacher,
        var profile: Profile
) : SugarRecord() {
    fun exists(): Boolean {
        return SugarRecord.count<SubjectTeacher>(SubjectTeacher::class.java, "PROFILE='" + profile.username + "' AND SUBJECT='" + subject.id + "' AND TEACHER='" + teacher.id + "'", arrayOf()) > 0
    }
}