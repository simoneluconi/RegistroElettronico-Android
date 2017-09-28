package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import java.util.*

data class Grade(@Expose @SerializedName("canceled") val mCanceled: Boolean,
                 @Expose @SerializedName("evtCode") val mCode: String,
                 @Expose @SerializedName("componentPos") val mComponentPos: Int,
                 @Expose @SerializedName("evtDate") val mDate: Date,
                 @Expose @SerializedName("subjectDesc") val mDescription: String,
                 @Expose @SerializedName("evtId") val mId: Int,
                 @Expose @SerializedName("notesForFamily") val mNotes: String,
                 @Expose @SerializedName("periodPos") val mPeriod: Int,
                 @Expose @SerializedName("periodDesc") val mPeriodName: String,
                 @Expose @SerializedName("displayValue") val mStringValue: String,
                 @Expose @SerializedName("subjectId") val mSubjectId: Int,
                 @Expose @SerializedName("componentDesc") val mType: String,
                 @Expose @SerializedName("underlined") val mUnderlined: Boolean,
                 @Expose @SerializedName("decimalValue") val mValue: Float,
                 @Expose @SerializedName("weightFactor") val mWeightFactor: Double,
                 var profile: Profile?
) : SugarRecord()