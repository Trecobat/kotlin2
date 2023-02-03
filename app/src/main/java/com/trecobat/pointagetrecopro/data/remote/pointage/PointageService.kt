package com.trecobat.pointagetrecopro.data.remote.pointage

import com.trecobat.pointagetrecopro.data.entities.Pointage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PointageService {
    @GET("pointageTrecopro/pointages")
    suspend fun getAllPointages() : Response<List<Pointage>>

    @GET("pointageTrecopro/pointages/{id}")
    suspend fun getPointage(@Path("id") id: Int): Response<Pointage>
}