package com.sharpdroid.registroelettronico.database.room.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.sharpdroid.registroelettronico.database.entities.Profile
import java.util.*

@Entity(tableName = "LESSON", foreignKeys = arrayOf(ForeignKey(entity = Profile::class, parentColumns = arrayOf(" id"), childColumns = arrayOf("user_id"))))
data class Lesson(
        @ColumnInfo(name = "M_ARGUMENT") var mArgument: String,
        @ColumnInfo(name = "M_AUTHOR_NAME") val mAuthorName: String,
        @ColumnInfo(name = "M_CLASS_DESCRIPTION") val mClassDescription: String,
        @ColumnInfo(name = "M_CODE") val mCode: String,
        @ColumnInfo(name = "M_DATE") val mDate: Date,
        @ColumnInfo(name = "M_DURATION") val mDuration: Int,
        @ColumnInfo(name = "M_HOUR_POSITION") val mHourPosition: Int,
        @ColumnInfo(name = "ID") @PrimaryKey val id: Long,
        @ColumnInfo(name = "M_SUBJECT_CODE") val mSubjectCode: String,
        @ColumnInfo(name = "M_SUBJECT_DESCRIPTION") val mSubjectDescription: String,
        @ColumnInfo(name = "M_SUBJECT_ID") val mSubjectId: Int,
        @ColumnInfo(name = "M_TYPE") val mType: String,
        @ColumnInfo(name = "PROFILE") var profile: Long
)