package com.sharpdroid.registroelettronico.Databases.Entities

import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Interfaces.Client.Average
import com.sharpdroid.registroelettronico.Utils.Account
import java.util.*

@Table
data class Grade(@Expose @SerializedName("evtCode") val mCode: String,
                 @Expose @SerializedName("componentPos") val mComponentPos: Int,
                 @Expose @SerializedName("evtDate") val mDate: Date,
                 @Expose @SerializedName("subjectDesc") val mDescription: String,
                 @Expose @SerializedName("evtId") @Unique val id: Int,
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
) {
    constructor() : this("", 0, Date(), "", 0, "", 0, "", "", 0, "", false, 0f, 0.0, null)

    companion object {
        fun getAverages(): List<Average> {
            val list = mutableListOf<Average>()
            val subjects = SugarRecord.find(Subject::class.java, "")

            return list
        }

        fun hasMarksSecondPeriod(context: Context): Boolean {
            return SugarRecord.count<Grade>(Grade::class.java, "M_PERIOD!=1 AND PROFILE=?", arrayOf(Account.with(context).user.toString())) > 0
        }
    }
}

data class GradeAPI(@Expose @SerializedName("grades") val grades: List<Grade>) {
    fun getGrades(profile: Profile): List<Grade> {
        grades.forEach { it.profile = profile }
        return grades
    }
}