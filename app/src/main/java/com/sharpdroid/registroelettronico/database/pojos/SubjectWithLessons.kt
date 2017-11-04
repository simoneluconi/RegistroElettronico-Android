package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import java.util.*


data class SubjectWithLessons(
        @Embedded
        var subject: Subject,
        @Relation(parentColumn = "ID", entityColumn = "SUBJECT")
        var subjectInfo: List<SubjectInfo>,
        @Relation(parentColumn = "ID", entityColumn = "M_SUBJECT_ID", entity = Lesson::class)
        var lessons: List<LessonMini>
) {
    constructor() : this(Subject(), emptyList(), emptyList())
}

data class LessonMini(
        @ColumnInfo(name = "M_ARGUMENT") var mArgument: String = "",
        @ColumnInfo(name = "M_AUTHOR_NAME") var mAuthorName: String = "",
        @ColumnInfo(name = "M_DATE") var mDate: Date,
        @ColumnInfo(name = "M_SUBJECT_DESCRIPTION") var mSubjectDescription: String = ""
) {
    constructor() : this("", "", Date(0), "")
}