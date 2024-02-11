package com.example.minimalworkingreadwritefile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow


class MainScreenViewModel():ViewModel() {

    val _state = MutableStateFlow("")
    val state = _state

    fun clearState(){
        _state.value = "T"
    }

    fun amendDataToState(data: String){
            _state.value = data
    }

}