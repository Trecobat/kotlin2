package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pointages")
data class Pointage(
    @PrimaryKey(autoGenerate = true) val poi_id: Int = 0,
    var poi_tache_id: Int = 0,
    var poi_debut: String = "",
    var poi_fin: String? = null,
    var poi_eq_id: Int = 0,
    var poi_type: String = "",
    var poi_coffret: Int? = null,
    var poi_remblais: Int? = null,
    var poi_corps_etat: String? = null,
    var poi_nature_erreur: String? = null,
    var poi_lat: Double? = null,
    var poi_lng: Double? = null,
    var poi_commentaire: String? = null,
    @Embedded var equipier: Equipier? = null
)
