package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/*
{
    "lessons": [
        {
            "evtId": 54431,
            "evtDate": "2017-09-13",
            "evtCode": "LSF0",
            "evtHPos": 4,
            "evtDuration": 1.0,
            "classDesc": "4FSA",
            "authorName": "LOREGIAN BRUNO FRANCESCO",
            "subjectId": 214413,
            "subjectCode": "FS",
            "subjectDesc": "FISICA",
            "lessonType": "Lezione",
            "lessonArg": "Teoria cinetica dei gas"
        }
    ]
}
 */
@Entity(tableName = "LESSON")
class Lesson(
        @ColumnInfo(name = "M_ARGUMENT") @Expose @SerializedName("lessonArg") var mArgument: String? = "",
        @ColumnInfo(name = "M_AUTHOR_NAME") @Expose @SerializedName("authorName") var mAuthorName: String = "",
        @ColumnInfo(name = "M_CLASS_DESCRIPTION") @Expose @SerializedName("classDesc") var mClassDescription: String = "",
        @ColumnInfo(name = "M_CODE") @Expose @SerializedName("evtCode") var mCode: String = "",
        @ColumnInfo(name = "M_DATE") @Expose @SerializedName("evtDate") var mDate: Date = Date(0),
        @ColumnInfo(name = "M_DURATION") @Expose @SerializedName("evtDuration") var mDuration: Double = -1.0,
        @ColumnInfo(name = "M_HOUR_POSITION") @Expose @SerializedName("evtHPos") var mHourPosition: Int = -1,
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") var id: Long = 0L,
        @ColumnInfo(name = "M_SUBJECT_CODE") @Expose @SerializedName("subjectCode") var mSubjectCode: String = "",
        @ColumnInfo(name = "M_SUBJECT_DESCRIPTION") @Expose @SerializedName("subjectDesc") var mSubjectDescription: String = "",
        @ColumnInfo(name = "M_SUBJECT_ID") @Expose @SerializedName("subjectId") var mSubjectId: Int = -1,
        @ColumnInfo(name = "M_TYPE") @Expose @SerializedName("lessonType") var mType: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L
)

class LessonAPI(@Expose @SerializedName("lessons") val lessons: List<Lesson>) {
    fun getLessons(profile: Profile): List<Lesson> {
        val id = profile.id
        lessons.forEach { it.profile = id }
        return lessons
    }
}