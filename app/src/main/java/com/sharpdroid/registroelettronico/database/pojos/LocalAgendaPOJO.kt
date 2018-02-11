package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.database.entities.Teacher
import com.sharpdroid.registroelettronico.utils.Metodi

data class LocalAgendaPOJO(
        @Embedded
        var event: LocalAgenda,
        @Relation(parentColumn = "TEACHER", entityColumn = "ID")
        var teacher: List<Teacher>,
        @Relation(parentColumn = "SUBJECT", entityColumn = "ID")
        var subject: List<Subject>,
        @Relation(parentColumn = "SUBJECT", entityColumn = "ID")
        var subjectInfo: List<SubjectInfo>
) {
    constructor() : this(LocalAgenda(), emptyList(), emptyList(), emptyList())

    fun getSubjectOrAuthor() = Metodi.capitalizeEach(subjectInfo.getOrNull(0)?.description
            ?: subject.getOrNull(0)?.description ?: teacher.getOrNull(0)?.teacherName ?: "", true)
}