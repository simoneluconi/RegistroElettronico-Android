package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.util.SparseArray
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
            "evtDuration": 1,
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
data class Lesson(
        @ColumnInfo(name = "M_ARGUMENT") @Expose @SerializedName("lessonArg") var mArgument: String = "",
        @ColumnInfo(name = "M_AUTHOR_NAME") @Expose @SerializedName("authorName") var mAuthorName: String = "",
        @ColumnInfo(name = "M_CLASS_DESCRIPTION") @Expose @SerializedName("classDesc") var mClassDescription: String = "",
        @ColumnInfo(name = "M_CODE") @Expose @SerializedName("evtCode") var mCode: String = "",
        @ColumnInfo(name = "M_DATE") @Expose @SerializedName("evtDate") var mDate: Date = Date(0),
        @ColumnInfo(name = "M_DURATION") @Expose @SerializedName("evtDuration") var mDuration: Int = -1,
        @ColumnInfo(name = "M_HOUR_POSITION") @Expose @SerializedName("evtHPos") var mHourPosition: Int = -1,
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") @Unique var id: Long = -1L,
        @ColumnInfo(name = "M_SUBJECT_CODE") @Expose @SerializedName("subjectCode") var mSubjectCode: String = "",
        @ColumnInfo(name = "M_SUBJECT_DESCRIPTION") @Expose @SerializedName("subjectDesc") var mSubjectDescription: String = "",
        @ColumnInfo(name = "M_SUBJECT_ID") @Expose @SerializedName("subjectId") var mSubjectId: Int = -1,
        @ColumnInfo(name = "M_TYPE") @Expose @SerializedName("lessonType") var mType: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long
) {
    constructor() : this("", "", "", "", Date(), 0, 0, 0, "", "", 0, "", -1)

    companion object {
        val lessonsOfSubject = SparseArray<List<Lesson>>()
        val allLessons = mutableListOf<Lesson>()

        fun setupCache(account: Long) {
            if (allLessons.isNotEmpty()) return

            val data = SugarRecord.find(Lesson::class.java, "PROFILE=$account")
            allLessons.addAll(data)

            val groupedBySubject = data.groupBy { it.mSubjectId }
            groupedBySubject.keys.forEach {
                lessonsOfSubject.put(it, groupedBySubject[it])
            }
        }

        fun clearCache() {
            lessonsOfSubject.clear()
            allLessons.clear()
        }
    }

}

data class LessonAPI(@Expose @SerializedName("lessons") val lessons: List<Lesson>) {
    fun getLessons(profile: Profile): List<Lesson> {
        val id = profile.id
        lessons.forEach { it.profile = id }
        return lessons
    }
}