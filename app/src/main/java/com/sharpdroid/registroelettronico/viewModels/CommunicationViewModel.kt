package com.sharpdroid.registroelettronico.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Communication
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class CommunicationViewModel : ViewModel() {
    private var livedata: LiveData<List<Communication>>? = null
    private var profile = 0L

    fun getCommunications(profile: Long): LiveData<List<Communication>> {
        if (livedata == null || this.profile != profile) {
            livedata = DatabaseHelper.database.communicationsDao().loadCommunications(profile)
            this.profile = profile
        }
        return livedata ?: throw NullPointerException("Communications' LiveData not yet initialized")
    }
}