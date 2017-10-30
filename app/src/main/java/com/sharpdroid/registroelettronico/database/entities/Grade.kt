package com.sharpdroid.registroelettronico.database.entities

import android.content.Context
import android.util.SparseArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.orm.SugarRecord
import com.orm.dsl.Table
import com.orm.dsl.Unique
import com.sharpdroid.registroelettronico.utils.Account
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
        private val subjectCache = SparseArray<Subject>()
        private val subjectInfoCache = SparseArray<SubjectInfo>()
        private val subjectOriginalNames = SparseArray<String>()

        fun clearSubjectCache() {
            subjectCache.clear()
            subjectInfoCache.clear()
            subjectOriginalNames.clear()
        }

        fun setupSubjectCache(account: Long) {
            val subjects = SugarRecord.find(Subject::class.java, "SUBJECT.ID IN (SELECT SUBJECT_TEACHER.SUBJECT FROM SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=?)", account.toString())
            val subjectInfos = SugarRecord.find(SubjectInfo::class.java, "SUBJECT_INFO.SUBJECT IN (SELECT SUBJECT.ID FROM SUBJECT) AND SUBJECT_INFO.PROFILE=?", account.toString())
            subjects.forEach { subjectCache.put(it.id.toInt(), it) }
            subjectInfos.forEach { subjectInfoCache.put(it.subject.id.toInt(), it) }
        }

        fun getAverages(grades: List<Grade>, order: String): List<Average> {

            val marksBySubject: Map<Int, List<Grade>> = grades.groupBy({
                subjectOriginalNames.put(it.mSubjectId, it.mDescription)

                return@groupBy it.mSubjectId
            }, { it })

            var toReturn = marksBySubject.keys.map {
                val average = Average()
                var title = subjectOriginalNames[it, ""]
                var target = 0f

                var temp = subjectInfoCache.indexOfKey(it)
                if (temp >= 0) {
                    println("$it found in subjectInfoCache at index $temp")
                    val t = subjectInfoCache.valueAt(temp)
                    if (!t.description.isEmpty())
                        title = t.description
                    target = t.target

                } else {
                    temp = subjectCache.indexOfKey(it)
                    if (temp >= 0) {
                        title = subjectCache.valueAt(temp).description
                        println("$it found in subjectCache at index $temp")
                    }
                }


                val marks = marksBySubject[it]!!

                marks.forEach {
                    average.avg += it.mValue
                    average.count += if (it.mValue != 0f) 1 else 0
                }

                //divide if contains valid marks
                if (average.count != 0)
                    average.avg /= average.count

                //add additional informations
                average.setCode(it)
                average.setTarget(target)
                average.setName(title)
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

data class GradeAPI(@Expose @SerializedName("grades") private val grades: List<Grade>) {
    fun getGrades(profile: Profile): List<Grade> {
        val id = profile.id
        grades.forEach { it.profile = id }
        return grades
    }
}