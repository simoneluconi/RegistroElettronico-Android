package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class GradesViewModel : ViewModel() {
    private var firstPeriod: LiveData<List<Average>>? = null
    private var secondPeriod: LiveData<List<Average>>? = null
    private var allPeriods: LiveData<List<Average>>? = null
    val order = MutableLiveData<String>()
    private var profile = 0L

    fun getGrades(profile: Long, period: Int): LiveData<List<Average>> {
        return when (period) {
            1 -> {
                if (firstPeriod == null || profile != this.profile) {
                    firstPeriod = DatabaseHelper.database.gradesDao().getAverages(profile, 1)
                }
                firstPeriod ?: throw NullPointerException("firstPeriod livedata not yet initialized")
            }
            3 -> {
                if (secondPeriod == null || profile != this.profile) {
                    secondPeriod = DatabaseHelper.database.gradesDao().getAverages(profile, 3)
                }
                secondPeriod ?: throw NullPointerException("secondPeriod livedata not yet initialized")
            }
            else -> {
                if (allPeriods == null || profile != this.profile) {
                    allPeriods = DatabaseHelper.database.gradesDao().getAllAverages(profile)
                }
                allPeriods ?: throw NullPointerException("allPeriods livedata not yet initialized")
            }
        }
    }

}