package com.trecobat.pointagetrecopro.data.remote.pointage

import com.trecobat.pointagetrecopro.data.remote.BaseDataSource
import javax.inject.Inject

class PointageDataSource @Inject constructor(
    private val pointageService: PointageService
): BaseDataSource() {

    suspend fun getPointages() = getResult { pointageService.getAllPointages() }
    suspend fun getPointage(id: Int) = getResult { pointageService.getPointage(id) }
}