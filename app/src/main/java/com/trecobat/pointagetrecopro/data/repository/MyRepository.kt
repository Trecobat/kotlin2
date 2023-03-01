package com.trecobat.pointagetrecopro.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.trecobat.pointagetrecopro.data.entities.*
import com.trecobat.pointagetrecopro.data.local.MyDao
import com.trecobat.pointagetrecopro.data.remote.BaseDataSource
import com.trecobat.pointagetrecopro.utils.Resource
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class MyRepository(
    private val remoteDataSource: BaseDataSource,
    private val localDataSource: MyDao
) {
    /***** AUTH *****/
    fun login(user: User) = performLoginOperation(
        networkCall = { remoteDataSource.login(user) },
        saveCallResult = { localDataSource.insertToken(it) }
    )

    fun getToken() = performLocalOperation (
        databaseQuery = { localDataSource.getToken() }
    )

    suspend fun deleteToken() = performDeleteAllOperation (
        deleteAll = { localDataSource.deleteAllToken() }
    )

    fun getAuthEquipe(email: String) = performGetOperation (
        databaseQuery = { localDataSource.getAuthEquipe(email) },
        networkCall = { remoteDataSource.getAuthEquipe(email) },
        saveCallResult = { localDataSource.insertEquipe(it) }
    )

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

    fun getPointagesOfTache(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getPointagesOfTache(id) },
        networkCall = { remoteDataSource.getPointagesOfTache(id) },
        saveCallResult = { localDataSource.insertAllPointages(it) }
    )

    suspend fun postPointage(data: Pointage) = performPostOperation(
        networkCall = { remoteDataSource.postPointage(data) },
        saveCallResult = { localDataSource.insertPointage(it) }
    )

    suspend fun updatePointage(pointage: Pointage) = performPostOperation(
        networkCall = { remoteDataSource.updatePointage(pointage) },
        saveCallResult = { localDataSource.updatePointage(it) }
    )

    fun getEquipiers() = performGetOperation(
        databaseQuery = { localDataSource.getAllEquipiers() },
        networkCall = { remoteDataSource.getEquipiers() },
        saveCallResult = { localDataSource.insertAllEquipiers(it) }
    )

    fun getEquipiersOfEquipe(equipe: Int = 0) = performGetOperation(
        databaseQuery = { localDataSource.getEquipiersOfEquipe(equipe) },
        networkCall = { remoteDataSource.getEquipiersOfEquipe(equipe) },
        saveCallResult = { localDataSource.insertAllEquipiers(it) }
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

    fun getBdcts() = performGetOperation(
        databaseQuery = { localDataSource.getAllBdcts() },
        networkCall = { remoteDataSource.getBdcts() },
        saveCallResult = { localDataSource.insertAllBdcts(it) }
    )

    fun getFilesOfTache(id: Int) = performGetOperation(
        databaseQuery = { localDataSource.getFilesOfTache(id) },
        networkCall = { remoteDataSource.getFilesOfTache(id) },
        saveCallResult = { localDataSource.insertAllFiles(it) }
    )

    suspend fun updateTache(tache: Tache) = performPostOperation(
        networkCall = { remoteDataSource.updateTache(tache) },
        saveCallResult = { localDataSource.updateTache(it) }
    )

    /***** AFFAIRE *****/
    fun getAffairesByAffIdOrCliNom(text: com.trecobat.pointagetrecopro.data.entities.String) = performGetOperation(
        databaseQuery = { localDataSource.getAffairesByAffIdOrCliNom(text) },
        networkCall = { remoteDataSource.getAffairesByAffIdOrCliNom(text) },
        saveCallResult = { localDataSource.insertAllAffaires(it) }
    )

    /***** PERFORM OPERATION *****/
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
            Timber.e(responseStatus.toString())
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
            emit(Resource.loading())

            val responseStatus = networkCall.invoke()
            if (responseStatus.status == Resource.Status.SUCCESS) {
                saveCallResult(responseStatus.data!!)
                emit(Resource.success(null))
            } else if (responseStatus.status == Resource.Status.ERROR) {
                emit(Resource.error(responseStatus.message!!))
            }
        }

    private fun <A> performLoginOperation(
        networkCall: suspend () -> Resource<A>,
        saveCallResult: suspend (A) -> Unit
    ): LiveData<Resource<A>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading())

            val responseStatus = networkCall.invoke()
            if (responseStatus.status == Resource.Status.SUCCESS) {
                saveCallResult(responseStatus.data!!)
                emit(Resource.success(responseStatus.data))
            } else if (responseStatus.status == Resource.Status.ERROR) {
                emit(Resource.error(responseStatus.message!!))
            }
        }

    private fun <T> performLocalOperation(
        databaseQuery: () -> LiveData<T>
    ): LiveData<Resource<T>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading())
            val source = databaseQuery.invoke().map { Resource.success(it) }
            emitSource(source)
        }

    private fun performDeleteAllOperation(
        deleteAll: suspend () -> Unit
    ): LiveData<Resource<Boolean>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading())
            try {
                deleteAll.invoke()
                emit(Resource.success(true))
            } catch (exception: Exception) {
                emit(Resource.error(exception.message ?: "Error occurred"))
            }
        }
}