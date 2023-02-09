package com.trecobat.pointagetrecopro.data.repository

import com.trecobat.pointagetrecopro.data.local.TacheDao
import com.trecobat.pointagetrecopro.data.remote.chantier.TacheDataSource
import com.trecobat.pointagetrecopro.utils.performGetOperation
import javax.inject.Inject

class TacheRepository @Inject constructor(
    private val remoteDataSource: TacheDataSource,
    private val localDataSource: TacheDao
) {

    fun getTache(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getTache(id) },
        networkCall = { remoteDataSource.getTache(id) },
        saveCallResult = { localDataSource.insert(it) }
    )

    fun getTaches() = performGetOperation(
        databaseQuery = { localDataSource.getAllTaches() },
        networkCall = { remoteDataSource.getTaches() },
        saveCallResult = { localDataSource.insertAll(it) }
    )

    fun getFilesOfTache(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getFilesOfTache(id) },
        networkCall = { remoteDataSource.getFilesOfTache(id) },
        saveCallResult = { localDataSource.insertAllFiles(it) }
    )
}