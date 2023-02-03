package com.trecobat.pointagetrecopro.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trecobat.pointagetrecopro.data.entities.Pointage

@Dao
interface PointageDao {

    @Query("SELECT * FROM pointages")
    fun getAllPointages() : LiveData<List<Pointage>>

    @Query("SELECT * FROM pointages WHERE poi_id = :id")
    fun getPointage(id: Int): LiveData<Pointage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pointages: List<Pointage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pointage: Pointage)


}