package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.FolderPOJO
import com.sharpdroid.registroelettronico.database.pojos.TeacherDidacticPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class DidatticaViewModel : ViewModel() {
    private var didattica: LiveData<List<TeacherDidacticPOJO>>? = null
    var selectedFolder = MutableLiveData<FolderPOJO>()
    var profile = MutableLiveData<Long>()

    fun getDidattica(profile: Long): LiveData<List<TeacherDidacticPOJO>> {
        if (didattica == null || this.profile.value != profile) {
            didattica = DatabaseHelper.database.foldersDao().getDidattica(profile)
        }
        this.profile.postValue(profile)
        return didattica ?: throw NullPointerException("Didattica's livedata not yet initialized")
    }
}