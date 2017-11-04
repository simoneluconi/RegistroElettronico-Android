package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Grade
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class GradesViewModel : ViewModel() {
    private var grades: LiveData<List<Grade>>? = null
    private var order: MutableLiveData<String>? = null

    fun getGrades(profile: Long): LiveData<List<Grade>> {
        if (grades == null) {
            grades = DatabaseHelper.database.gradesDao().getGrades(profile)
        }
        return grades ?: throw NullPointerException("Grades' livedata not yet initialized")
    }

    fun getOrder(): LiveData<String> {
        if (order == null) {
            order = MutableLiveData()
        }
        return order ?: throw NullPointerException("Order (grades) not yet initialized")
    }

    fun setOrder(value: String) {
        order?.postValue(value)
    }
}