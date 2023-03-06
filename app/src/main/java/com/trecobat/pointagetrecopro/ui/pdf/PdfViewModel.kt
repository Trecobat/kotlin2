package com.trecobat.pointagetrecopro.ui.pdf

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor() : ViewModel() {
    private val _affId = MutableLiveData<Int>()
    private val _catLabel = MutableLiveData<String>()

    fun startAffId(affId: Int) {
        _affId.value = affId
    }

    fun startCatLabel(catLabel: String) {
        _catLabel.value = catLabel
    }
}