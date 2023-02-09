package com.trecobat.pointagetrecopro.data.remote.chantier

import com.trecobat.pointagetrecopro.data.remote.BaseDataSource
import javax.inject.Inject

class TacheDataSource @Inject constructor(
    private val tacheService: TacheService
): BaseDataSource() {

    suspend fun getTaches() = getResult { tacheService.getAllTaches() }
    suspend fun getTache(id: Int) = getResult { tacheService.getTache(id) }
    suspend fun getFilesOfTache(id: Int) = getResult { tacheService.getFilesOfTache(id) }
}