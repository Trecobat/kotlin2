package com.trecobat.pointagetrecopro.ui.addmarche

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.trecobat.pointagetrecopro.data.entities.Affaire
import com.trecobat.pointagetrecopro.data.entities.BdcType
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import com.trecobat.pointagetrecopro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddMarcheViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {

    val bdcTypes = getCorpsEtat()
    val test = getAffairesByAffIdOrCliNom("%150602%")

    private fun getCorpsEtat(): LiveData<Resource<List<BdcType>>> {
        return repository.getBdcts()
    }

    fun getAffairesByAffIdOrCliNom(text: String): LiveData<Resource<List<Affaire>>> {
        return repository.getAffairesByAffIdOrCliNom(text)
    }
}