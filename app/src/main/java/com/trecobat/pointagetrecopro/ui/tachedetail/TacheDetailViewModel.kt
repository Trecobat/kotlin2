package com.trecobat.pointagetrecopro.ui.tachedetail

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.Equipier
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource

class TacheDetailViewModel @ViewModelInject constructor(
    private val repository: MyRepository
) : ViewModel() {

    // La fonction switchMap est là pour observer le changement d'état de _id,
    // c'est quand il obtient une valeur que le contenu de la fonction est exécuté

    private val _id = MutableLiveData<Int>()

    val tache = _id.switchMap { id ->
        repository.getTache(id)
    }
//    var tache: LiveData<Resource<Tache>> = _tache

    val pointages = _id.switchMap { id ->
        repository.getPointagesOfTache(id)
    }

    var gedFiles = _id.switchMap { id ->
        repository.getFilesOfTache(id)
    }

    var corpsEtat = _id.switchMap {
        repository.getBdcts()
    }

    var equipiers = _id.switchMap {
        repository.getEquipiersOfEquipe(5)
    }

    fun start(id: Int) {
        _id.value = id
    }

    suspend fun postPointage(data: Pointage) : LiveData<Resource<Nothing?>> {
        return repository.postPointage(data)
    }

    suspend fun updatePointage(pointage: Pointage): LiveData<Resource<Nothing?>> {
        return repository.updatePointage(pointage)
    }

    fun getEquipiers(equipe: Int = 0): LiveData<Resource<List<Equipier>>> {
        return repository.getEquipiers(equipe)
    }

    fun getEquipiersOfEquipe(equipe: Int = 0): LiveData<Resource<List<Equipier>>> {
        return repository.getEquipiersOfEquipe(equipe)
    }
}
