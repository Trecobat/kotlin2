package com.trecobat.pointagetrecopro.ui.tachedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.repository.PointageRepository
import com.trecobat.pointagetrecopro.data.repository.TacheRepository
import com.trecobat.pointagetrecopro.utils.Resource

class TacheDetailViewModel @ViewModelInject constructor(
    private val tacheRepository: TacheRepository,
    private val pointageRepository: PointageRepository
) : ViewModel() {

    // La fonction switchMap est là pour observer le changement d'état de _id,
    // c'est quand il obtient une valeur que le contenu de la fonction est exécuté

    private val _id = MutableLiveData<Int>()

    private val _tache = _id.switchMap { id ->
        tacheRepository.getTache(id)
    }
    var tache: LiveData<Resource<Tache>> = _tache

    var gedFiles = _id.switchMap { id ->
        tacheRepository.getFilesOfTache(id)
    }

    fun start(id: Int) {
        _id.value = id
    }

    suspend fun postPointage(data: Pointage) : LiveData<Resource<Nothing?>> {
        return pointageRepository.postPointage(data)
    }

    fun getTache(id: Int): LiveData<Resource<Tache>> {
        return tacheRepository.getTache(id)
    }
}
