package com.trecobat.pointagetrecopro.ui.pointagedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.repository.PointageRepository
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PointageDetailViewModel @Inject constructor(
    private val repository: PointageRepository
) : ViewModel() {

    private val _id = MutableLiveData<Int>()

    private val _pointage = _id.switchMap { id ->
        repository.getPointage(id)
    }
    val pointage: LiveData<Resource<Pointage>> = _pointage


    fun start(id: Int) {
        _id.value = id
    }

}