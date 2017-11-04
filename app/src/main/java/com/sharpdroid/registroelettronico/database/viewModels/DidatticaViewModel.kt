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

    fun getDidattica(profile: Long): LiveData<List<TeacherDidacticPOJO>> {
        if (didattica == null) {
            didattica = DatabaseHelper.database.foldersDao().getDidattica(profile)
        }
        return didattica ?: throw NullPointerException("Didattica's livedata not yet initialized")
    }
}