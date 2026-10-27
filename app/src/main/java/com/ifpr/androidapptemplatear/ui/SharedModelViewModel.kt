package com.ifpr.androidapptemplatear

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedModelViewModel : ViewModel() {

    private val _selectedModel =
        MutableLiveData("02_ogro_guerra.glb")

    val selectedModel: LiveData<String>
        get() = _selectedModel

    fun selectModel(modelPath: String) {
        if (modelPath.isNotBlank()) {
            _selectedModel.value = modelPath
        }
    }
}
