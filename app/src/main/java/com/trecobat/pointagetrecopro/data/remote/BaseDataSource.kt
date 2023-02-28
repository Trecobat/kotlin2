package com.trecobat.pointagetrecopro.data.remote

import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache
import com.trecobat.pointagetrecopro.data.entities.User
import com.trecobat.pointagetrecopro.utils.Resource
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class BaseDataSource @Inject constructor(
    private val myService: MyService
) {

    private suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) return Resource.success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Resource<T> {
        Timber.d(message)
        return Resource.error("Network call has failed for a following reason: $message")
    }

    /***** AUTH *****/
    suspend fun login(user: User) = getResult { myService.login(user) }

    /***** POINTAGE *****/
    suspend fun getPointages() = getResult { myService.getAllPointages() }
    suspend fun getPointagesOfTache(id: Int) = getResult { myService.getPointagesOfTache(id) }
    suspend fun getBdcts() = getResult { myService.getAllBdct() }
    suspend fun getPointage(id: Int) = getResult { myService.getPointage(id) }
    suspend fun postPointage(data: Pointage) = getResult { myService.postPointage(data) }
    suspend fun getEquipiers() = getResult { myService.getAllEquipiers() }
    suspend fun getAuthEquipe(email: String) = getResult { myService.getAuthEquipe(email) }
    suspend fun getEquipiersOfEquipe(equipe: Int) = getResult { myService.getEquipiersOfEquipe(equipe) }
    suspend fun updatePointage(pointage: Pointage) = getResult { myService.updatePointage(pointage.poi_id, pointage) }

    /***** TACHE *****/
    suspend fun getTaches() = getResult { myService.getAllTaches() }
    suspend fun getTache(id: Int) = getResult { myService.getTache(id) }
    suspend fun getFilesOfTache(id: Int) = getResult { myService.getFilesOfTache(id) }
    suspend fun updateTache(tache: Tache) = getResult { myService.updateTache(tache.id, tache) }

    /***** AFFAIRE *****/
    suspend fun getAffairesByAffIdOrCliNom(text: String) = getResult { myService.getAffairesByAffIdOrCliNom(text) }
}