package com.trecobat.pointagetrecopro.ui.taches

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.repository.TacheRepository
import com.trecobat.pointagetrecopro.utils.Resource

class TachesViewModel @ViewModelInject constructor(
    private val repository: TacheRepository
) : ViewModel() {

    val taches = refreshData()

    private fun refreshData(): LiveData<Resource<List<Tache>>> {
        return repository.getTaches()
    }
}
