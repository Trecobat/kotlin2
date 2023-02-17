package com.trecobat.pointagetrecopro.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.trecobat.pointagetrecopro.data.entities.*

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

    @Query("SELECT * FROM pointages WHERE pointages.poi_tache_id = :tache")
    fun getPointagesOfTache(tache: Int): LiveData<List<Pointage>>

    @Query("SELECT * FROM bdc_type")
    fun getAllBdcts(): LiveData<List<BdcType>>

    @Query("SELECT * FROM pointages WHERE poi_id = :id")
    fun getPointage(id: Int): LiveData<Pointage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPointages(pointages: List<Pointage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBdcts(bdc_types: List<BdcType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPointage(pointage: Pointage)

//    @Query("SELECT *, IF(equipiers.eevp_eqvp_id = :equipe, 1, 0) AS equipe FROM equipiers ORDER BY equipe")
//    fun getAllEquipiers(equipe: Int = 0): LiveData<List<Equipier>>

    @Query("SELECT * FROM equipiers ORDER BY eevp_prenom")
    fun getAllEquipiers(): LiveData<List<Equipier>>

    @Query("SELECT * FROM equipiers WHERE eqvp_id = :equipe")
    fun getEquipiersOfEquipe(equipe: Int): LiveData<List<Equipier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEquipiers(equipiers: List<Equipier>)

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