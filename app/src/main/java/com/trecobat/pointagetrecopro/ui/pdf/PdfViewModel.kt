package com.trecobat.pointagetrecopro.ui.pdf

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PdfViewModel @ViewModelInject constructor() : ViewModel() {
    private val _affId = MutableLiveData<Int>()
    private val _catLabel = MutableLiveData<String>()

    fun startAffId(affId: Int) {
        _affId.value = affId
    }

    fun startCatLabel(catLabel: String) {
        _catLabel.value = catLabel
    }
}