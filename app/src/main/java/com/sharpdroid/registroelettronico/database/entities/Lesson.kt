package com.sharpdroid.registroelettronico.database.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Table
import com.orm.dsl.Unique
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
@Table
data class Lesson(
        @Expose @SerializedName("lessonArg") val mArgument: String,
        @Expose @SerializedName("authorName") val mAuthorName: String,
        @Expose @SerializedName("classDesc") val mClassDescription: String,
        @Expose @SerializedName("evtCode") val mCode: String,
        @Expose @SerializedName("evtDate") val mDate: Date,
        @Expose @SerializedName("evtDuration") val mDuration: Int,
        @Expose @SerializedName("evtHPos") val mHourPosition: Int,
        @Expose @SerializedName("evtId") @Unique val id: Long,
        @Expose @SerializedName("subjectCode") val mSubjectCode: String,
        @Expose @SerializedName("subjectDesc") val mSubjectDescription: String,
        @Expose @SerializedName("subjectId") val mSubjectId: Int,
        @Expose @SerializedName("lessonType") val mType: String,
        var profile: Long
) {
    constructor() : this("", "", "", "", Date(), 0, 0, 0, "", "", 0, "", -1)
}

data class LessonAPI(@Expose @SerializedName("lessons") val lessons: List<Lesson>) {
    fun getLessons(profile: Profile): List<Lesson> {
        val id = profile.id
        lessons.forEach { it.profile = id }
        return lessons
    }
}