package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import java.util.*

data class Lesson(
        @Expose @SerializedName("lessonArg") val mArgument: String,
        @Expose @SerializedName("authorName") val mAuthorName: String,
        @Expose @SerializedName("classDesc") val mClassDescription: String,
        @Expose @SerializedName("evtCode") val mCode: String,
        @Expose @SerializedName("evtDate") val mDate: Date,
        @Expose @SerializedName("evtDuration") val mDuration: Int,
        @Expose @SerializedName("evtHPos") val mHourPosition: Int,
        @Expose @SerializedName("evtId") val mId: Int,
        @Expose @SerializedName("subjectCode") val mSubjectCode: String,
        @Expose @SerializedName("subjectDesc") val mSubjectDescription: String,
        @Expose @SerializedName("subjectId") val mSubjectId: Int,
        @Expose @SerializedName("lessonType") val mType: String,
        var profile: Profile?
) : SugarRecord() {
    constructor() : this("", "", "", "", Date(), 0, 0, 0, "", "", 0, "", null)
}

data class LessonAPI(@Expose @SerializedName("lessons") val lessons: List<Lesson>) {
    fun getLessons(profile: Profile): List<Lesson> {
        lessons.forEach { it.profile = profile }
        return lessons
    }
}