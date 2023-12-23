package com.rb.anytextwiget.jetpackUI

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rb.anytextwiget.WidgetData

class TestViewModel(currentData: WidgetData): ViewModel() {
    private val _data = MutableLiveData(currentData)
    var data by mutableStateOf(currentData.widgetText)

    fun onDataChanged(newt: String) {
        data = newt
    }
}