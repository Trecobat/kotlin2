package com.trecobat.pointagetrecopro.ui.taches

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.repository.TacheRepository

class TachesViewModel @ViewModelInject constructor(
    repository: TacheRepository
) : ViewModel() {

    val taches = repository.getTaches()
}
