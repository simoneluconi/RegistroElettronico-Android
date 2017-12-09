package com.sharpdroid.registroelettronico.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.SubjectPOJO

class AddTimetableItemViewModel : ViewModel() {
    val id = MutableLiveData<Long>()
    val color = MutableLiveData<Int>()
    val day = MutableLiveData<Int>()
    val start = MutableLiveData<String>()
    val end = MutableLiveData<String>()
    val subject = MutableLiveData<SubjectPOJO>()
    val subjects = mutableListOf<SubjectPOJO>()
}