package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.entities.LocalGrade
import com.sharpdroid.registroelettronico.database.pojos.AverageType
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import io.reactivex.Flowable

class SubjectDetailsViewModel : ViewModel() {
    var averages: LiveData<List<AverageType>>? = null
    var grades: LiveData<List<Grade>>? = null
    var localGrades: Flowable<List<LocalGrade>>? = null
    var subjectInfo: LiveData<SubjectPOJO>? = null
    var animateTarget = MutableLiveData<Boolean>()
    var animateLocalMarks = MutableLiveData<Boolean>()

    fun getAverages(profile: Long, subject: Long, period: Int): LiveData<List<AverageType>> {
        if (averages == null) {
            averages = if (period != -1) DatabaseHelper.database.gradesDao().getPeriodTypeAverage(profile, subject, period) else DatabaseHelper.database.gradesDao().getAllTypeAverage(profile, subject)
        }
        return averages ?: throw NullPointerException("Averages' liveData not yet initialized")
    }

    fun getGrades(profile: Long, subject: Long, period: Int): LiveData<List<Grade>> {
        if (grades == null) {
            grades = if (period != -1) DatabaseHelper.database.gradesDao().periodGrades(profile, subject, period) else DatabaseHelper.database.gradesDao().allPeriodsGrades(profile, subject)
        }
        return grades ?: throw NullPointerException("Grades' liveData not yet initialized")
    }

    fun getLocalGrades(profile: Long, subject: Long, period: Int): Flowable<List<LocalGrade>> {
        if (localGrades == null) {
            localGrades = if (period != -1) DatabaseHelper.database.gradesDao().periodLocalGrades(profile, subject, period) else DatabaseHelper.database.gradesDao().allPeriodsLocalGrades(profile, subject)
        }
        return localGrades ?: throw NullPointerException("Averages' liveData not yet initialized")
    }

    fun getSubject(subject: Long): LiveData<SubjectPOJO> {
        if (subjectInfo == null) {
            subjectInfo = DatabaseHelper.database.subjectsDao().getSubjectInfo(subject)
        }
        return subjectInfo ?: throw NullPointerException("Subject liveData not yet initialized")
    }
}