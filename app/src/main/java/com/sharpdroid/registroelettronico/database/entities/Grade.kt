package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import java.io.Serializable
import java.util.*

@Entity(tableName = "GRADE")
data class Grade(
        @ColumnInfo(name = "M_CODE") @Expose @SerializedName("evtCode") var mCode: String = "",
        @ColumnInfo(name = "M_COMPONENT_POS") @Expose @SerializedName("componentPos") var mComponentPos: Int = -1,
        @ColumnInfo(name = "M_DATE") @Expose @SerializedName("evtDate") var mDate: Date = Date(0),
        @ColumnInfo(name = "M_DESCRIPTION") @Expose @SerializedName("subjectDesc") var mDescription: String = "",
        @ColumnInfo(name = "ID") @PrimaryKey @Expose @SerializedName("evtId") var id: Long = 0L,
        @ColumnInfo(name = "M_NOTES") @Expose @SerializedName("notesForFamily") var mNotes: String = "",
        @ColumnInfo(name = "M_PERIOD") @Expose @SerializedName("periodPos") var mPeriod: Int = -1,
        @ColumnInfo(name = "M_PERIOD_NAME") @Expose @SerializedName("periodDesc") var mPeriodName: String = "",
        @ColumnInfo(name = "M_STRING_VALUE") @Expose @SerializedName("displayValue") var mStringValue: String = "",
        @ColumnInfo(name = "M_SUBJECT_ID") @Expose @SerializedName("subjectId") var mSubjectId: Int = -1,
        @ColumnInfo(name = "M_TYPE") @Expose @SerializedName("componentDesc") var mType: String = "",
        @ColumnInfo(name = "M_UNDERLINED") @Expose @SerializedName("underlined") var mUnderlined: Boolean = false,
        @ColumnInfo(name = "M_VALUE") @Expose @SerializedName("decimalValue") var mValue: Float = 0f,
        @ColumnInfo(name = "M_WEIGHT_FACTOR") @Expose @SerializedName("weightFactor") var mWeightFactor: Double = 0.0,
        @ColumnInfo(name = "PROFILE") var profile: Long = -1L
) : Serializable {

    fun isExcluded(): Boolean {
        return DatabaseHelper.database.gradesDao().countExcluded(id) == 1L
    }

    fun exclude(exclude: Boolean) {
        if (exclude) {
            DatabaseHelper.database.gradesDao().exclude(ExcludedMark(id))
        } else {
            DatabaseHelper.database.gradesDao().dontExclude(ExcludedMark(id))
        }
    }

    companion object {
        fun hasMarksSecondPeriod(profile: Long): Boolean {
            return DatabaseHelper.database.query("SELECT * FROM GRADE WHERE PROFILE=? AND M_PERIOD!=1", arrayOf(profile)).moveToFirst()
        }
    }
}

class GradeAPI(@Expose @SerializedName("grades") private val grades: List<Grade>) {
    fun getGrades(profile: Profile): List<Grade> {
        val id = profile.id
        grades.forEach { it.profile = id }
        return grades
    }
}