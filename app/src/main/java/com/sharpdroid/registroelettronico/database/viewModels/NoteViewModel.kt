package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.entities.Note
import com.sharpdroid.registroelettronico.database.room.DatabaseHelper

class NoteViewModel : ViewModel() {
    private var notes: LiveData<List<Note>>? = null

    fun getNotes(profile: Long): LiveData<List<Note>> {
        if (notes == null) {
            notes = DatabaseHelper.database.notesDao().getNotes(profile)
        }
        return notes ?: throw NullPointerException("Notes' livedata not yet initialized")
    }
}