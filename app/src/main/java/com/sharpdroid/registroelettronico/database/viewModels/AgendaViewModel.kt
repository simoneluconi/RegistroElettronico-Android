package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.LocalAgenda
import com.sharpdroid.registroelettronico.database.entities.RemoteAgenda
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class AgendaViewModel : ViewModel() {
    private var remote: LiveData<List<RemoteAgenda>>? = null
    private var local: LiveData<List<LocalAgenda>>? = null

    fun getRemote(profile: Long): LiveData<List<RemoteAgenda>> {
        if (remote == null) remote = DatabaseHelper.database.eventsDao().getRemote(profile)
        return remote ?: throw NullPointerException("Remote LiveData not yet initialized")
    }

    fun getLocal(profile: Long): LiveData<List<LocalAgenda>> {
        if (local == null) local = DatabaseHelper.database.eventsDao().getLocal(profile)
        return local ?: throw NullPointerException("Remote LiveData not yet initialized")
    }
}