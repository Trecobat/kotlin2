package com.trecobat.pointagetrecopro.ui.pointages

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.repository.PointageRepository

class PointagesViewModel @ViewModelInject constructor(
    private val repository: PointageRepository
) : ViewModel() {

    val pointages = repository.getPointages()
}
