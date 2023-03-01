package com.trecobat.pointagetrecopro.data.remote

import com.trecobat.pointagetrecopro.data.entities.*
import retrofit2.Response
import retrofit2.http.*

interface MyService {
    /***** AUTH *****/
    @POST("login")
    suspend fun login(@Body user: User): Response<Token>

    /***** TACHE *****/
    @GET("api/pointageTrecopro/chantiers")
    suspend fun getAllTaches() : Response<List<Tache>>

    @GET("api/pointageTrecopro/chantiers/{id}")
    suspend fun getTache(@Path("id") id: Int): Response<Tache>

    @GET("api/pointageTrecopro/chantiers/{id}/pointages")
    suspend fun getPointagesOfTache(@Path("id") id: Int): Response<List<Pointage>>

    @GET("api/pointageTrecopro/chantiers/{id}/plans")
    suspend fun getFilesOfTache(@Path("id") id: Int): Response<List<GedFiles>>

    @POST("api/pointageTrecopro/pointages/{id}")
    suspend fun updateTache(@Path("id") id: Int, @Body data: Tache): Response<Tache>

    /***** POINTAGE *****/
    @GET("api/pointageTrecopro/pointages")
    suspend fun getAllPointages() : Response<List<Pointage>>

    @GET("api/pointageTrecopro/bdct")
    suspend fun getAllBdct() : Response<List<BdcType>>

    @GET("api/pointageTrecopro/pointage/{id}")
    suspend fun getPointage(@Path("id") id: Int): Response<Pointage>

    @POST("api/pointageTrecopro/pointages")
    suspend fun postPointage(@Body data: Pointage): Response<Pointage>

    @POST("api/pointageTrecopro/pointages/{id}")
    suspend fun updatePointage(@Path("id") id: Int, @Body data: Pointage): Response<Pointage>

    /***** Equipiers *****/
    @GET("api/pointageTrecopro/equipiers")
    suspend fun getAllEquipiers(): Response<List<Equipier>>

    @GET("api/pointageTrecopro/equipes/{email}")
    suspend fun getAuthEquipe(@Path("email") email: String): Response<Equipe>

    @GET("api/pointageTrecopro/equipiers/{equipe}")
    suspend fun getEquipiersOfEquipe(@Path("equipe") equipe: Int): Response<List<Equipier>>

    /***** Affaires *****/
    @POST("api/pointageTrecopro/affaires")
    suspend fun getAffairesByAffIdOrCliNom(@Body text: com.trecobat.pointagetrecopro.data.entities.String): Response<com.trecobat.pointagetrecopro.data.entities.String>
//    suspend fun getAffairesByAffIdOrCliNom(@Body text: String): Response<List<Affaire>>
}