package com.trecobat.pointagetrecopro.ui.taches

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TachesViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    val taches = refreshData()

    private fun refreshData(): LiveData<Resource<List<Tache>>> {
        return repository.getTaches()
    }

    suspend fun updateTache(tache: Tache): LiveData<Resource<Tache>> {
        return repository.updateTache(tache)
    }
}
