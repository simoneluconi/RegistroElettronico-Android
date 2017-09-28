package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import java.util.*

data class Note(
        @Expose @SerializedName("authorName") val mAuthor: String,
        @Expose @SerializedName("evtDate") val mDate: Date,
        @Expose @SerializedName("evtId") val mId: Int,
        @Expose @SerializedName("readStatus") val mStatus: Boolean,
        @Expose @SerializedName("evtText") val mText: String,
        @Expose @SerializedName("warningType") val mWarning: String,
        val mType: String,
        var profile: Profile?
) : SugarRecord()