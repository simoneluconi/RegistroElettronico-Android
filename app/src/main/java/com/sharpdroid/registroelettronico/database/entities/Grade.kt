package com.sharpdroid.registroelettronico.database.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.util.SparseArray
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "GRADE")
class Grade(
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
) {
    companion object {
        private val subjectCache = SparseArray<Subject>()
        private val subjectInfoCache = SparseArray<SubjectInfo>()
        private val subjectOriginalNames = SparseArray<String>()

        fun clearSubjectCache() {
            subjectCache.clear()
            subjectInfoCache.clear()
            subjectOriginalNames.clear()
        }

        fun setupSubjectCache(account: Long) {/*
            val subjects = SugarRecord.find(Subject::class.java, "SUBJECT.ID IN (SELECT SUBJECT_TEACHER.SUBJECT FROM SUBJECT_TEACHER WHERE SUBJECT_TEACHER.PROFILE=?)", account.toString())
            val subjectInfos = SugarRecord.find(SubjectInfo::class.java, "SUBJECT_INFO.SUBJECT IN (SELECT SUBJECT.ID FROM SUBJECT) AND SUBJECT_INFO.PROFILE=?", account.toString())
            subjects.forEach { subjectCache.put(it.id.toInt(), it) }
            subjectInfos.forEach { subjectInfoCache.put(it.subject.id.toInt(), it) }*/
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
                average.code = it
                average.target = target
                average.name = title
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
                    toReturn.sortedBy { it.name.toLowerCase() }
                }
            }
            return toReturn
        }

        fun hasMarksSecondPeriod(context: Context): Boolean {
            return false//SugarRecord.count<Grade>(Grade::class.java, "M_PERIOD!=1 AND PROFILE=?", arrayOf(Account.with(context).user.toString())) > 0
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