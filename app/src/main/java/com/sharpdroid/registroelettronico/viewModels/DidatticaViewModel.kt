package com.sharpdroid.registroelettronico.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.sharpdroid.registroelettronico.database.pojos.FolderPOJO

class DidatticaViewModel : ViewModel() {
    var selectedFolder = MutableLiveData<FolderPOJO>()
    val scrollPosition = MutableLiveData<Int>()
}