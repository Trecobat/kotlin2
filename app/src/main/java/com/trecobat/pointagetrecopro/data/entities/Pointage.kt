package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pointages")
data class Pointage(
    @PrimaryKey(autoGenerate = true) val poi_id: Int = 0,
    val poi_tache_id: Int,
    val poi_debut: String,
    val poi_fin: String? = null,
    val poi_eq_id: Int,
    val poi_lat: Double? = null,
    val poi_lng: Double? = null
)
