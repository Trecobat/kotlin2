package com.trecobat.pointagetrecopro.ui.addmarchets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.data.entities.MyString
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddMarcheTsViewModel @Inject constructor(
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

    fun getAffairesByAffIdOrCliNom(text: MyString): LiveData<Resource<List<Affaire>>> {
        return repository.getAffairesByAffIdOrCliNom(text)
    }

    fun addTache(tache: PostTache): LiveData<Resource<Tache>> {
        return repository.addTache(tache)
    }
}