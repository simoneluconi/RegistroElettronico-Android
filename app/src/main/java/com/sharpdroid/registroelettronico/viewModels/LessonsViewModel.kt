package com.sharpdroid.registroelettronico.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.SubjectWithLessons
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper
import io.reactivex.Flowable

class LessonsViewModel : ViewModel() {
    var selected = MutableLiveData<SubjectWithLessons>()
    var profile = 0L
    private var subjects: Flowable<List<SubjectWithLessons>>? = null

    var query = MutableLiveData<String>()

    fun getSubjectsWithLessons(profile: Long): Flowable<List<SubjectWithLessons>> {
        //if profile has changed, then re-create
        if (subjects == null || this.profile != profile) {
            subjects = DatabaseHelper.database.subjectsDao().getSubjectsAndLessons(profile)
            this.profile = profile
        }
        return subjects ?: throw NullPointerException("Subjects' LiveData not yet initialized")
    }

}