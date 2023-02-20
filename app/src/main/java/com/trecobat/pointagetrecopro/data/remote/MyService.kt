package com.trecobat.pointagetrecopro.data.remote

import android.graphics.Point
import com.trecobat.pointagetrecopro.data.entities.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MyService {
    /***** TACHE *****/
    @GET("pointageTrecopro/chantiers")
    suspend fun getAllTaches() : Response<List<Tache>>

    @GET("pointageTrecopro/chantiers/{id}")
    suspend fun getTache(@Path("id") id: Int): Response<Tache>

    @GET("pointageTrecopro/chantiers/{id}/pointages")
    suspend fun getPointagesOfTache(@Path("id") id: Int): Response<List<Pointage>>

    @GET("pointageTrecopro/chantiers/{id}/plans")
    suspend fun getFilesOfTache(@Path("id") id: Int): Response<List<GedFiles>>

    /***** POINTAGE *****/
    @GET("pointageTrecopro/pointages")
    suspend fun getAllPointages() : Response<List<Pointage>>

    @GET("pointageTrecopro/bdct")
    suspend fun getAllBdct() : Response<List<BdcType>>

    @GET("pointageTrecopro/pointage/{id}")
    suspend fun getPointage(@Path("id") id: Int): Response<Pointage>

    @POST("pointageTrecopro/pointages")
    suspend fun postPointage(@Body data: Pointage): Response<Pointage>

    @POST("pointageTrecopro/pointages/{id}")
    suspend fun updatePointage(@Path("id") id: Int, @Body data: Pointage): Response<Pointage>

    /***** Equipiers *****/
    @GET("pointageTrecopro/equipiers")
    suspend fun getAllEquipiers(): Response<List<Equipier>>
    @GET("pointageTrecopro/equipiers/{equipe}")
    suspend fun getEquipiersOfEquipe(@Path("equipe") equipe: Int): Response<List<Equipier>>
}