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
    private var order = MutableLiveData<String>()

    fun getFirstPeriod(profile: Long): LiveData<List<Average>> {
        if (firstPeriod == null) {
            firstPeriod = DatabaseHelper.database.gradesDao().getAverages(profile, 1)

        }
        return firstPeriod ?: throw NullPointerException("firstPeriod livedata not yet initialized")
    }

    fun getSecondPeriod(profile: Long): LiveData<List<Average>> {
        if (secondPeriod == null) {
            secondPeriod = DatabaseHelper.database.gradesDao().getAverages(profile, 3)

        }
        return secondPeriod ?: throw NullPointerException("secondPeriod livedata not yet initialized")
    }

    fun getAllPeriods(profile: Long): LiveData<List<Average>> {
        if (allPeriods == null) {
            allPeriods = DatabaseHelper.database.gradesDao().getAllAverages(profile)
        }
        return allPeriods ?: throw NullPointerException("allPeriods livedata not yet initialized")
    }

    fun getOrder(): LiveData<String> {
        return order
    }

    fun setOrder(value: String) {
        order.postValue(value)
    }
}