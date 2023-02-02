package com.trecobat.pointagetrecopro.ui.pointages

import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.repository.PointageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PointagesViewModel @Inject constructor(
    repository: PointageRepository
) : ViewModel() {

    val pointages = repository.getPointages()

}