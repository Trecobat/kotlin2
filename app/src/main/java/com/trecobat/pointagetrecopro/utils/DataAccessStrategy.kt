package com.trecobat.pointagetrecopro.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.trecobat.pointagetrecopro.utils.Resource.Status.ERROR
import com.trecobat.pointagetrecopro.utils.Resource.Status.SUCCESS
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

fun <T, A> performGetOperation(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val source = databaseQuery.invoke().map { Resource.success(it) }
        emitSource(source)

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == SUCCESS) {
            saveCallResult(responseStatus.data!!)

        } else if (responseStatus.status == ERROR) {
            emit(Resource.error(responseStatus.message!!))
            emitSource(source)
        }
    }

fun <A> performPostOperation(
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<Nothing?>> =
    liveData(Dispatchers.IO) {
        Timber.i("performPostOperation")
        emit(Resource.loading())

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == SUCCESS) {
            Timber.i("performPostOperation SUCCESS")
            saveCallResult(responseStatus.data!!)
            emit(Resource.success(null))
        } else if (responseStatus.status == ERROR) {
            Timber.i("performPostOperation ERROR")
            emit(Resource.error(responseStatus.message!!))
        } else {
            Timber.i("performPostOperation ni SUCCESS ni ERROR")
        }
    }