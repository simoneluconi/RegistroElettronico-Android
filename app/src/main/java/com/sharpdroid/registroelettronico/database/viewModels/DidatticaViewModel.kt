package com.sharpdroid.registroelettronico.database.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.FolderPOJO

class DidatticaViewModel : ViewModel() {
    var selectedFolder = MutableLiveData<FolderPOJO>()
    var profile = MutableLiveData<Long>()
}