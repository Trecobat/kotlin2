package com.trecobat.pointagetrecopro.ui.tachedetail

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.trecobat.pointagetrecopro.data.entities.Equipier
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.helper.NetworkHelper
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.qualifiers.ActivityContext

class TacheDetailViewModel @ViewModelInject constructor(
    private val repository: MyRepository
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

    var corpsEtat = _id.switchMap {
        repository.getBdcts()
    }

    fun start(id: Int) {
        _id.value = id
    }

    suspend fun postPointage(data: Pointage) : LiveData<Resource<Nothing?>> {
        return repository.postPointage(data)
    }

    fun getTache(id: Int): LiveData<Resource<Tache>> {
        return repository.getTache(id)
    }

    fun getEquipiers(equipe: Int = 0): LiveData<Resource<List<Equipier>>> {
        return repository.getEquipiers(equipe)
    }
}
