package com.trecobat.pointagetrecopro.ui.tachedetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.Equipier
import com.trecobat.pointagetrecopro.data.entities.Pointage
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

    fun start(id: Int) {
        _id.value = id
    }

    suspend fun postPointage(data: Pointage) : LiveData<Resource<Nothing?>> {
        return repository.postPointage(data)
    }

    suspend fun updatePointage(pointage: Pointage): LiveData<Resource<Nothing?>> {
        return repository.updatePointage(pointage)
    }

    fun getEquipiers(): LiveData<Resource<List<Equipier>>> {
        return repository.getEquipiers()
    }

    fun getEquipiersOfEquipe(): LiveData<Resource<List<Equipier>>> {
        val equipe = System.getProperty("equipe")
        if ( equipe != null ) {
            return repository.getEquipiersOfEquipe(Integer.parseInt(equipe))
        }
        // Inutile parce que cette fonction utilise l'equipe du user connecté qui en a forcément une.
        // Cependant il faut quand même vérifier que c'est pas null par rapport au typage dans l'entité
        return repository.getEquipiersOfEquipe(0)
    }
}
