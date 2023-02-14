package com.trecobat.pointagetrecopro.data.repository

import com.trecobat.pointagetrecopro.data.entities.PendingRequest
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.local.MyDao
import com.trecobat.pointagetrecopro.data.remote.BaseDataSource
import com.trecobat.pointagetrecopro.utils.performGetOperation
import com.trecobat.pointagetrecopro.utils.performPostOperation

class MyRepository (
    private val remoteDataSource: BaseDataSource,
    private val localDataSource: MyDao
) {
    /***** PENDING REQUEST *****/
    fun sendRequest(url: String, body: String) {
        val request = PendingRequest(url = url, body = body)
        localDataSource.insertPendingRequest(request)
    }

    /***** POINTAGE *****/
    fun getPointage(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getPointage(id) },
        networkCall = { remoteDataSource.getPointage(id) },
        saveCallResult = { localDataSource.insertPointage(it) }
    )

    fun getPointages() = performGetOperation(
        databaseQuery = { localDataSource.getAllPointages() },
        networkCall = { remoteDataSource.getPointages() },
        saveCallResult = { localDataSource.insertAllPointages(it) }
    )

    suspend fun postPointage(data: Pointage) = performPostOperation(
        networkCall = { remoteDataSource.postPointage(data) },
        saveCallResult = { localDataSource.insertPointage(it) }
    )

    /***** TACHE *****/
    fun getTache(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getTache(id) },
        networkCall = { remoteDataSource.getTache(id) },
        saveCallResult = { localDataSource.insertTache(it) }
    )

    fun getTaches() = performGetOperation(
        databaseQuery = { localDataSource.getAllTaches() },
        networkCall = { remoteDataSource.getTaches() },
        saveCallResult = { localDataSource.insertAllTaches(it) }
    )

    fun getFilesOfTache(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getFilesOfTache(id) },
        networkCall = { remoteDataSource.getFilesOfTache(id) },
        saveCallResult = { localDataSource.insertAllFiles(it) }
    )
}