package com.trecobat.pointagetrecopro.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.data.entities.PendingRequest
import com.trecobat.pointagetrecopro.data.entities.Pointage
import com.trecobat.pointagetrecopro.data.entities.Tache

@Dao
interface MyDao {

    /***** PENDING REQUEST *****/
    @Insert
    fun insertPendingRequest(request: PendingRequest)

    @Query("SELECT * FROM pending_requests")
    fun getAllPendingRequests(): List<PendingRequest>

    @Delete
    fun deletePendingRequest(request: PendingRequest)

    /***** POINTAGE *****/
    @Query("SELECT * FROM pointages")
    fun getAllPointages(): LiveData<List<Pointage>>

    @Query("SELECT * FROM pointages WHERE poi_id = :id")
    fun getPointage(id: Int): LiveData<Pointage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPointages(pointages: List<Pointage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointage(pointage: Pointage)

    /***** TACHE *****/
    @Query("SELECT * FROM taches")
    fun getAllTaches() : LiveData<List<Tache>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTaches(taches: List<Tache>)

    @Query("SELECT * FROM taches WHERE id = :id")
    fun getTache(id: Int): LiveData<Tache>

    @Query("SELECT * FROM ged_files WHERE gdf_fo_id = :gdf_fo_id")
    fun getFile(gdf_fo_id: String): LiveData<GedFiles>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTache(tache: Tache)

    @Query("SELECT * FROM ged_files WHERE gdf_tache_id = :id")
    fun getFilesOfTache(id: Int): LiveData<List<GedFiles>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFiles(ged_files: List<GedFiles>)

    @Query("UPDATE ged_files SET local_storage = :localStorage WHERE gdf_fo_id = :gdfFoId")
    fun updateLocalStorage(gdfFoId: String, localStorage: String)
}