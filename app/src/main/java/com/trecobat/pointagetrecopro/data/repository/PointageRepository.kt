package com.trecobat.pointagetrecopro.data.repository

import androidx.lifecycle.LiveData
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.local.PointageDao
import com.trecobat.pointagetrecopro.data.remote.pointage.PointageDataSource
import com.trecobat.pointagetrecopro.utils.performGetOperation
import com.trecobat.pointagetrecopro.utils.performPostOperation
import javax.inject.Inject

class PointageRepository @Inject constructor(
    private val remoteDataSource: PointageDataSource,
    private val localDataSource: PointageDao
) {

    fun getPointage(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getPointage(id) },
        networkCall = { remoteDataSource.getPointage(id) },
        saveCallResult = { localDataSource.insert(it) }
    )

    fun getPointages() = performGetOperation(
        databaseQuery = { localDataSource.getAllPointages() },
        networkCall = { remoteDataSource.getPointages() },
        saveCallResult = { localDataSource.insertAll(it) }
    )

    suspend fun postPointage(data: Pointage) = performPostOperation(
        networkCall = { remoteDataSource.postPointage(data) },
        saveCallResult = { localDataSource.insert(it) }
    )
}