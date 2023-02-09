package com.trecobat.pointagetrecopro.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trecobat.pointagetrecopro.data.entities.GedFiles
import com.trecobat.pointagetrecopro.data.entities.Tache

@Dao
interface TacheDao {

    @Query("SELECT * FROM taches")
    fun getAllTaches() : LiveData<List<Tache>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taches: List<Tache>)

    @Query("SELECT * FROM taches WHERE id = :id")
    fun getTache(id: Int): LiveData<Tache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tache: Tache)

    @Query("SELECT * FROM ged_files WHERE gdf_tache_id = :id")
    fun getFilesOfTache(id: Int): LiveData<List<GedFiles>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFiles(ged_files: List<GedFiles>)
}