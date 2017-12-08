package com.sharpdroid.registroelettronico.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Note
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class NoteViewModel : ViewModel() {
    private var notes: LiveData<List<Note>>? = null
    private var profile = 0L

    fun getNotes(profile: Long): LiveData<List<Note>> {
        if (notes == null || profile != this.profile) {
            notes = DatabaseHelper.database.notesDao().getNotes(profile)
            this.profile = profile
        }
        return notes ?: throw NullPointerException("Notes' livedata not yet initialized")
    }
}