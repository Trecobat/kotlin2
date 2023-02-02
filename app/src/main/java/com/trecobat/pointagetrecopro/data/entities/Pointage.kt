package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pointages")
data class Pointage(
    @PrimaryKey val poi_id: Int,
    val poi_tache_id: Int,
    val poi_debut: String,
    val poi_fin: String,
    val poi_eq_id: Int,
    val poi_poi_lat: Float,
    val poi_lng: Float,
)
