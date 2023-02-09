package com.trecobat.pointagetrecopro.ui.tachedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.repository.TacheRepository
import com.trecobat.pointagetrecopro.utils.Resource

class TacheDetailViewModel @ViewModelInject constructor(
    private val repository: TacheRepository
) : ViewModel() {

    // La fonction switchMap est là pour observer le changement d'état de _id,
    // c'est quand il obtient une valeur que le contenu de la fonction est exécuté

    private val _id = MutableLiveData<Int>()

    private val _tache = _id.switchMap { id ->
        repository.getTache(id)
    }
    var tache: LiveData<Resource<Tache>> = _tache

    var gedFiles = _id.switchMap { id ->
        repository.getFilesOfTache(id)
    }

    fun start(id: Int) {
        _id.value = id
    }

}
