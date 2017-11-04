package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "NOTE")
class Note(
        @ColumnInfo(name = "M_AUTHOR") @Expose @SerializedName("authorName") var mAuthor: String = "",
        @ColumnInfo(name = "M_DATE") @Expose @SerializedName("evtDate") var mDate: Date = Date(0),
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") var id: Long = 0L,
        @ColumnInfo(name = "M_STATUS") @Expose @SerializedName("readStatus") var mStatus: Boolean = false,
        @ColumnInfo(name = "M_TEXT") @Expose @SerializedName("evtText") var mText: String = "",
        @ColumnInfo(name = "M_WARNING") @Expose @SerializedName("warningType") var mWarning: String = "",
        @ColumnInfo(name = "M_TYPE") var mType: String = "",
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L
)

class NoteAPI(
        @Expose @SerializedName("NTTE") private val ntte: List<Note>,
        @Expose @SerializedName("NTCL") private val ntcl: List<Note>,
        @Expose @SerializedName("NTWN") private val ntwn: List<Note>,
        @Expose @SerializedName("NTST") private val ntst: List<Note>
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