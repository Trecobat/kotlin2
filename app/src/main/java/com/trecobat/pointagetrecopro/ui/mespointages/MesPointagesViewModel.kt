package com.trecobat.pointagetrecopro.ui.mespointages

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MesPointagesViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    val pointages = refreshData()

    private fun refreshData(): LiveData<Resource<List<Pointage>>> {
        return repository.getPointages()
    }

    suspend fun updatePointage(pointage: Pointage): LiveData<Resource<Pointage>> {
        return repository.updatePointage(pointage)
    }
}