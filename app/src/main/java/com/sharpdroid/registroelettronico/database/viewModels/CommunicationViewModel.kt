package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class CommunicationViewModel : ViewModel() {
    private var livedata: LiveData<List<Communication>>? = null

    fun getCommunications(profile: Long): LiveData<List<Communication>> {
        if (livedata == null) livedata = DatabaseHelper.database.communicationsDao().loadCommunications(profile)
        return livedata ?: throw NullPointerException("Communications' LiveData not yet initialized")
    }
}