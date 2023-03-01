package com.trecobat.pointagetrecopro.ui.addmarchets

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.data.entities.String
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource

class AddMarcheTsViewModel @ViewModelInject constructor(
    private val repository: MyRepository
) : ViewModel() {

    val type = MutableLiveData<kotlin.String>()

    fun start(_type: kotlin.String) {
        type.value = _type
    }

    val bdcTypes = type.switchMap {
        getCorpsEtat()
    }

    private fun getCorpsEtat(): LiveData<Resource<List<BdcType>>> {
        return repository.getBdcts()
    }

    fun getAffairesByAffIdOrCliNom(text: String): LiveData<Resource<List<Affaire>>> {
        return repository.getAffairesByAffIdOrCliNom(text)
    }

    fun addTache(tache: PostTache): LiveData<Resource<Tache>> {
        return repository.addTache(tache)
    }
}