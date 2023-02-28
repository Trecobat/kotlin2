package com.trecobat.pointagetrecopro.ui.addmarche

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.room.Query
import com.trecobat.pointagetrecopro.data.entities.Affaire
import com.trecobat.pointagetrecopro.data.entities.BdcType
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource

class AddMarcheViewModel @ViewModelInject constructor(
    private val repository: MyRepository
) : ViewModel() {

    val bdcTypes = getCorpsEtat()

    private fun getCorpsEtat(): LiveData<Resource<List<BdcType>>> {
        return repository.getBdcts()
    }

    fun getAffairesByAffIdOrCliNom(text: String): LiveData<Resource<List<Affaire>>> {
        return repository.getAffairesByAffIdOrCliNom(text)
    }
}