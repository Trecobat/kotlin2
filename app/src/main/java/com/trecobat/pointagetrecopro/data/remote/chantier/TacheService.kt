package com.trecobat.pointagetrecopro.data.remote.chantier

import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.data.entities.Tache
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

//import retrofit2.http.Path

interface TacheService {
    @GET("pointageTrecopro/chantiers")
    suspend fun getAllTaches() : Response<List<Tache>>

    @GET("pointageTrecopro/chantiers/{id}")
    suspend fun getTache(@Path("id") id: Int): Response<Tache>

    @GET("pointageTrecopro/chantiers/{id}/plans")
    suspend fun getFilesOfTache(@Path("id") id: Int): Response<List<GedFiles>>
}