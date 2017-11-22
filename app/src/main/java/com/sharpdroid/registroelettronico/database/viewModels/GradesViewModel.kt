package com.sharpdroid.registroelettronico.database.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.preference.PreferenceManager
import com.sharpdroid.registroelettronico.database.entities.Average
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class GradesViewModel(app: Application) : AndroidViewModel(app) {
    private var firstPeriod: LiveData<List<Average>>? = null
    private var secondPeriod: LiveData<List<Average>>? = null
    private var allPeriods: LiveData<List<Average>>? = null
    val order = MutableLiveData<String>()
    private var firstProfile = 0L
    private var secondProfile = 0L
    private var thirdProfile = 0L
    var selected = 0

    init {
        order.value = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("order", "")
    }

    fun getGrades(profile: Long, period: Int): LiveData<List<Average>> = when (period) {
        1 -> {
            if (firstPeriod == null || profile != this.firstProfile) {
                firstPeriod = DatabaseHelper.database.gradesDao().getAverages(profile, 1)
                this.firstProfile = profile
            }
            firstPeriod ?: throw NullPointerException("firstPeriod livedata not yet initialized")
        }
        3 -> {
            if (secondPeriod == null || profile != this.secondProfile) {
                secondPeriod = DatabaseHelper.database.gradesDao().getAverages(profile, 3)
                this.secondProfile = profile
            }
            secondPeriod ?: throw NullPointerException("secondPeriod livedata not yet initialized")
        }
        else -> {
            if (allPeriods == null || profile != this.thirdProfile) {
                allPeriods = DatabaseHelper.database.gradesDao().getAllAverages(profile)
                this.thirdProfile = profile
            }
            allPeriods ?: throw NullPointerException("allPeriods livedata not yet initialized")
        }
    }

}