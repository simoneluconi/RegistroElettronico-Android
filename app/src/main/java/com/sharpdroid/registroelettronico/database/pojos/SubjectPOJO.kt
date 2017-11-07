package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo

class SubjectPOJO(
        @Embedded
        var subject: Subject = Subject(),
        @Relation(parentColumn = "ID", entityColumn = "SUBJECT")
        var subjectInfo: List<SubjectInfo> = emptyList()
)