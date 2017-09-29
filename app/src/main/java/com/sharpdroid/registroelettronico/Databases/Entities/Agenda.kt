package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import java.util.*

data class Agenda(
        @Expose @SerializedName("authorName") val mAuthor: String,
        @Expose @SerializedName("evtDatetimeBegin") val mBegin: Date,
        @Expose @SerializedName("classDesc") val mClassDescription: String,
        @Expose @SerializedName("evtDatetimeEnd") val mEnd: Date,
        @Expose @SerializedName("evtCode") val mEventCode: String,
        @Expose @SerializedName("isFullDay") val mFullDay: Boolean,
        @Expose @SerializedName("evtId") val mId: Int,
        @Expose @SerializedName("notes") val mNotes: String,
        @Expose @SerializedName("subjectDesc") val mSubjectDescription: String,
        var profile: Profile?
) : SugarRecord()

data class AgendaAPI(@Expose @SerializedName("agenda") val agenda: List<Agenda>) {
    fun getAgenda(profile: Profile): List<Agenda> {
        agenda.forEach { it.profile = profile }
        return agenda
    }
}