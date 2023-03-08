package com.trecobat.pointagetrecopro.ui.pointagedivers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.Equipier
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PointageDiversViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    var pointageDivers: LiveData<Resource<List<Pointage>>> = repository.getPointagesDivers()

    suspend fun postPointage(data: Pointage) : LiveData<Resource<List<Pointage>>> {
        return repository.postPointage(data)
    }

    suspend fun updatePointage(pointage: Pointage): LiveData<Resource<Pointage>> {
        return repository.updatePointage(pointage)
    }

    fun getEquipiers(): LiveData<Resource<List<Equipier>>> {
        return repository.getEquipiers()
    }
}