package com.sharpdroid.registroelettronico.database.pojos

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.sharpdroid.registroelettronico.database.entities.Lesson
import com.sharpdroid.registroelettronico.database.entities.Subject
import com.sharpdroid.registroelettronico.database.entities.SubjectInfo
import com.sharpdroid.registroelettronico.utils.Metodi
import com.sharpdroid.registroelettronico.utils.or
import java.util.*


data class SubjectWithLessons(
        @Embedded
        var subject: Subject = Subject(),
        @Relation(parentColumn = "ID", entityColumn = "SUBJECT")
        var subjectInfo: List<SubjectInfo> = emptyList(),
        @Relation(parentColumn = "ID", entityColumn = "M_SUBJECT_ID", entity = Lesson::class)
        var lessons: List<LessonMini> = emptyList()
) {
        fun getSubjectName() = Metodi.capitalizeEach(subjectInfo.getOrNull(0)?.description.or(subject.description))
}

data class LessonMini(
        @ColumnInfo(name = "M_ARGUMENT") var mArgument: String = "",
        @ColumnInfo(name = "M_AUTHOR_NAME") var mAuthorName: String = "",
        @ColumnInfo(name = "M_DATE") var mDate: Date = Date(0),
        @ColumnInfo(name = "M_SUBJECT_DESCRIPTION") var mSubjectDescription: String = ""
)