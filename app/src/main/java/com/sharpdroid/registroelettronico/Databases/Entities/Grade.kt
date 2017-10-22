package com.sharpdroid.registroelettronico.Databases.Entities

import android.content.Context
import android.util.SparseArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.Activities.or
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
        val subjectCache = SparseArray<Subject>()

        fun getAverages(context: Context, grades: List<Grade>, order: String): List<Average> {

            val map: Map<Subject?, List<Grade>> = grades.groupBy({
                if (subjectCache.indexOfKey(it.mSubjectId) < 0 && subjectCache.get(it.mSubjectId) == null) {
                    subjectCache.put(it.mSubjectId, SugarRecord.findById(Subject::class.java, it.mSubjectId))
                } else {
                    println("load from cache")
                }
                return@groupBy subjectCache[it.mSubjectId]
            }, { it })

            var toReturn = map.keys.map {
                val average = Average()
                println(it)
                val info = it?.getInfo(context)

                val marks = map[it]!!

                marks.forEach {
                    average.avg += it.mValue
                    average.count += if (it.mValue != 0f) 1 else 0
                }
                if (average.count != 0)
                    average.avg /= average.count
                average.setCode(it?.id?.toInt() ?: -1)
                average.setTarget(info?.target ?: 0f)
                average.setName(info?.description.or(it?.description.orEmpty()))
                return@map average
            }

            toReturn = when (order) {
                "avg" -> {
                    toReturn.sortedByDescending { it.avg }
                }
                "count" -> {
                    toReturn.sortedByDescending { it.count }
                }
                else -> {
                    toReturn.sortedByDescending { it.name.toLowerCase() }
                }
            }
            return toReturn
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