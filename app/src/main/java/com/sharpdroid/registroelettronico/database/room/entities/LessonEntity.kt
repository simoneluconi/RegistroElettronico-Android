package com.sharpdroid.registroelettronico.database.room.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "LESSON")
class LessonEntity(
        @ColumnInfo(name = "M_ARGUMENT") var mArgument: String = "",
        @ColumnInfo(name = "M_AUTHOR_NAME") var mAuthorName: String = "",
        @ColumnInfo(name = "M_CLASS_DESCRIPTION") var mClassDescription: String = "",
        @ColumnInfo(name = "M_CODE") var mCode: String = "",
        @ColumnInfo(name = "M_DATE") var mDate: Date = Date(0),
        @ColumnInfo(name = "M_DURATION") var mDuration: Int = 0,
        @ColumnInfo(name = "M_HOUR_POSITION") var mHourPosition: Int = 0,
        @ColumnInfo(name = "M_SUBJECT_CODE") var mSubjectCode: String = "",
        @ColumnInfo(name = "M_SUBJECT_DESCRIPTION") var mSubjectDescription: String = "",
        @ColumnInfo(name = "M_SUBJECT_ID") var mSubjectId: Int = 0,
        @ColumnInfo(name = "M_TYPE") var mType: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long = -1
) {
    @ColumnInfo(name = "ID")
    @PrimaryKey
    var id: Long = -1L
}