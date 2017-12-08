package com.sharpdroid.registroelettronico.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.LocalAgendaPOJO
import com.sharpdroid.registroelettronico.database.pojos.RemoteAgendaPOJO
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class AgendaViewModel : ViewModel() {
    private var remote: LiveData<List<RemoteAgendaPOJO>>? = null
    private var local: LiveData<List<LocalAgendaPOJO>>? = null
    private var localProfile = 0L
    private var remoteProfile = 0L

    fun getRemote(profile: Long): LiveData<List<RemoteAgendaPOJO>> {
        if (remote == null || this.remoteProfile != profile) {
            remote = DatabaseHelper.database.eventsDao().getRemote(profile)
            this.remoteProfile = profile
        }
        return remote ?: throw NullPointerException("Remote LiveData not yet initialized")
    }

    fun getLocal(profile: Long): LiveData<List<LocalAgendaPOJO>> {
        if (local == null || this.localProfile != profile) {
            local = DatabaseHelper.database.eventsDao().getLocal(profile)
            this.localProfile = profile
        }
        return local ?: throw NullPointerException("Remote LiveData not yet initialized")
    }
}