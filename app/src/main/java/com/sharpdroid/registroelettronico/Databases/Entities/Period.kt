package com.sharpdroid.registroelettronico.Databases.Entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import java.util.*

data class Period(
        @Expose @SerializedName("periodCode") val mCode: String,
        @Expose @SerializedName("periodDesc") val mDescription: String,
        @Expose @SerializedName("miurDivisionCode") val mDivisionCode: String,
        @Expose @SerializedName("dateEnd") val mEnd: Date,
        @Expose @SerializedName("isFinal") val mFinal: Boolean,
        @Expose @SerializedName("periodPos") val mPosition: Int,
        @Expose @SerializedName("dateStart") val mStart: Date,
        var profile: Profile?
) : SugarRecord()