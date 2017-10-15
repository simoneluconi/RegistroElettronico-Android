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
                 @Expose @SerializedName("evtId") @Unique val id: Long,
                 @Expose @SerializedName("notesForFamily") val mNotes: String,
                 @Expose @SerializedName("periodPos") val mPeriod: Int,
                 @Expose @SerializedName("periodDesc") val mPeriodName: String,
                 @Expose @SerializedName("displayValue") val mStringValue: String,
                 @Expose @SerializedName("subjectId") val mSubjectId: Int,
                 @Expose @SerializedName("componentDesc") val mType: String,
                 @Expose @SerializedName("underlined") val mUnderlined: Boolean,
                 @Expose @SerializedName("decimalValue") val mValue: Float,
                 @Expose @SerializedName("weightFactor") val mWeightFactor: Double,
                 var profile: Long
) {
    constructor() : this("", 0, Date(), "", 0, "", 0, "", "", 0, "", false, 0f, 0.0, -1L)

    companion object {
        fun getAverages(context: Context, where: String, order: String): List<Average> {
            return SugarRecord.findWithQuery(Average::class.java, "SELECT " +
                    "0 as ID, " +
                    "M_DESCRIPTION as `NAME`, " +
                    "M_SUBJECT_ID as `CODE`, " +
                    "AVG(M_VALUE) as `AVG`, " +
                    "(SELECT SUBJECT.TARGET FROM SUBJECT WHERE SUBJECT.ID=GRADE.M_SUBJECT_ID LIMIT 1) as `TARGET`, " +
                    "COUNT(M_VALUE) as `COUNT` " +
                    "FROM GRADE " +
                    "WHERE $where PROFILE=? " +
                    "GROUP BY code $order", Account.with(context).user.toString())
        }

        fun hasMarksSecondPeriod(context: Context): Boolean {
            return SugarRecord.count<Grade>(Grade::class.java, "M_PERIOD!=1 AND PROFILE=?", arrayOf(Account.with(context).user.toString())) > 0
        }
    }
}

data class GradeAPI(@Expose @SerializedName("grades") val grades: List<Grade>) {
    fun getGrades(profile: Profile): List<Grade> {
        val id = profile.id
        grades.forEach { it.profile = id }
        return grades
    }
}