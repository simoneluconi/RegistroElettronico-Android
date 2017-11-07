package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.SubjectWithLessons
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class LessonsViewModel : ViewModel() {
    private var subjects: LiveData<List<SubjectWithLessons>>? = null
    var selected = MutableLiveData<SubjectWithLessons>()
    var profile = 0L

    var query = MutableLiveData<String>()

    fun getSubjectsWithLessons(profile: Long): LiveData<List<SubjectWithLessons>> {
        if (subjects == null || this.profile != profile) {
            subjects = DatabaseHelper.database.subjectsDao().getSubjectWithLessons(profile)
            this.profile = profile
        }
        return subjects ?: throw NullPointerException("Subjects' LiveData not yet initialized")
    }

}