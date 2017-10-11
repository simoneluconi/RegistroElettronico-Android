package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.dsl.Table
import java.util.*

@Table
data class Note(
        @Expose @SerializedName("authorName") val mAuthor: String,
        @Expose @SerializedName("evtDate") val mDate: Date,
        @Expose @SerializedName("evtId") val id: Long,
        @Expose @SerializedName("readStatus") val mStatus: Boolean,
        @Expose @SerializedName("evtText") val mText: String,
        @Expose @SerializedName("warningType") val mWarning: String,
        var mType: String,
        var profile: Long
) {
    constructor() : this("", Date(), 0, false, "", "", "", -1L)
}

data class NoteAPI(
        @Expose @SerializedName("NTTE") val ntte: List<Note>,
        @Expose @SerializedName("NTCL") val ntcl: List<Note>,
        @Expose @SerializedName("NTWN") val ntwn: List<Note>,
        @Expose @SerializedName("NTST") val ntst: List<Note>
) {
    fun getNotes(profile: Profile): List<Note> {
        val newList = ArrayList<Note>()
        val id = profile.id
        ntte.forEach {
            it.mType = "NTTE"
            it.profile = id
        }
        ntcl.forEach {
            it.mType = "NTCL"
            it.profile = id
        }
        ntwn.forEach {
            it.mType = "NTWN"
            it.profile = id
        }
        ntst.forEach {
            it.mType = "NTST"
            it.profile = id
        }
        newList.addAll(ntte)
        newList.addAll(ntcl)
        newList.addAll(ntwn)
        newList.addAll(ntst)
        return newList
    }
}