package com.trecobat.pointagetrecopro.ui.pointagedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource

class PointageDetailViewModel @ViewModelInject constructor(
    private val repository: MyRepository
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
