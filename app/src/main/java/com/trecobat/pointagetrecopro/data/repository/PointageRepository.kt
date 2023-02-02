package com.trecobat.pointagetrecopro.data.repository

import com.trecobat.pointagetrecopro.data.local.PointageDao
import com.trecobat.pointagetrecopro.data.remote.pointage.PointageDataSource
import com.trecobat.pointagetrecopro.utils.performGetOperation
import javax.inject.Inject

class PointageRepository @Inject constructor(
    private val remoteDataSource: PointageDataSource,
    private val localDataSource: PointageDao
) {
    fun getPointages() = performGetOperation(
        databaseQuery = { localDataSource.getAllPointages() },
        networkCall = { remoteDataSource.getPointages() },
        saveCallResult = { localDataSource.insertAll(it) }
    )

    fun getPointage(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getPointage(id) },
        networkCall = { remoteDataSource.getPointage(id) },
        saveCallResult = { localDataSource.insert(it) }
    )
}