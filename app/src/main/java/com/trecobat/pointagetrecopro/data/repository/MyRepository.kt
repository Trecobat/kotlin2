package com.trecobat.pointagetrecopro.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.trecobat.pointagetrecopro.data.entities.PendingRequest
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.local.MyDao
import com.trecobat.pointagetrecopro.data.remote.BaseDataSource
import com.trecobat.pointagetrecopro.utils.Resource
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

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



    private fun <T, A> performGetOperation(
        databaseQuery: () -> LiveData<T>,
        networkCall: suspend () -> Resource<A>,
        saveCallResult: suspend (A) -> Unit
    ): LiveData<Resource<T>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading())
            val source = databaseQuery.invoke().map { Resource.success(it) }
            emitSource(source)

            val responseStatus = networkCall.invoke()
            if (responseStatus.status == Resource.Status.SUCCESS) {
                saveCallResult(responseStatus.data!!)

            } else if (responseStatus.status == Resource.Status.ERROR) {
                emit(Resource.error(responseStatus.message!!))
                emitSource(source)
            }
        }

    private fun <A> performPostOperation(
        networkCall: suspend () -> Resource<A>,
        saveCallResult: suspend (A) -> Unit
    ): LiveData<Resource<Nothing?>> =
        liveData(Dispatchers.IO) {
            Timber.i("performPostOperation")
            emit(Resource.loading())

            val responseStatus = networkCall.invoke()
            if (responseStatus.status == Resource.Status.SUCCESS) {
                Timber.i("performPostOperation SUCCESS")
                saveCallResult(responseStatus.data!!)
                emit(Resource.success(null))
            } else if (responseStatus.status == Resource.Status.ERROR) {
                Timber.i("performPostOperation ERROR")
                emit(Resource.error(responseStatus.message!!))
            } else {
                Timber.i("performPostOperation ni SUCCESS ni ERROR")
            }
        }
}